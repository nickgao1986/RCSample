package com.example.nickgao.contacts;

import android.content.Context;
import android.text.TextUtils;

import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.logging.EngLog;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.network.RestApiErrorCodes;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.service.AbstractService;
import com.example.nickgao.service.IRequestFactory;
import com.example.nickgao.service.request.RcRestRequest;
import com.example.nickgao.service.response.RestListResponse;
import com.example.nickgao.utils.HttpUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nick.gao on 1/31/17.
 */

public class CloudFavoriteService extends AbstractService {
    public static final int REQUEST_TYPE_DOWNLOAD = 1;
    public static final int REQUEST_TYPE_UPLOAD = 2;
    public static final String HEADER_E_TAG = "ETag";
    public static final boolean IS_DEBUG = true;

    protected int mRequestType = REQUEST_TYPE_DOWNLOAD;
    protected boolean mExecutionFinished = false;
    protected List<CloudFavoriteContactInfo> mCloudFavorites = new ArrayList<>();

    private void setRequestType(int requestType) {
        mRequestType = requestType;
    }

    public int getRequestType() {
        return mRequestType;
    }

    public boolean executionFinished() {
        synchronized (this) {
            return mExecutionFinished;
        }
    }


    public List<CloudFavoriteContactInfo> getCloudFavorites() {
        return mCloudFavorites;
    }

    public CloudFavoriteService(IRequestFactory requestFactory) {
        super(requestFactory);
    }

    public void downloadCloudFavorites() {
        setRequestType(REQUEST_TYPE_DOWNLOAD);
        RcRestRequest<RestListResponse<CloudFavoriteContactInfo>> request = this.mRequestFactory.getCloudFavoriteList();

        String favoriteETag = CurrentUserSettings.getSettings().getCurrentFavoriteETag();
        if(!TextUtils.isEmpty(favoriteETag)){
            request.addHeader(HttpUtils.IF_NONE_MATCH,String.format("\"%s\"", favoriteETag));
        }

        doRequest(request);
    }

    public void uploadCloudFavorites(String requestBody) {
        setRequestType(REQUEST_TYPE_UPLOAD);
        RcRestRequest<RestListResponse<CloudFavoriteContactInfo>> request = this.mRequestFactory.updateCloudFavoriteList(requestBody);
        doRequest(request);
    }

    private void doRequest(RcRestRequest<RestListResponse<CloudFavoriteContactInfo>> request) {
        final Context context = RingCentralApp.getContextRC();

        request.registerOnRequestListener(new RcRestRequest.OnRequestListener<RestListResponse<CloudFavoriteContactInfo>>() {
            @Override
            public void onSuccess(RcRestRequest<RestListResponse<CloudFavoriteContactInfo>> request, RestListResponse<CloudFavoriteContactInfo> response) {
                if (LogSettings.MARKET) {
                    MktLog.i(TAG, "onSuccess");
                }

                mCloudFavorites.addAll(Arrays.asList(response.getRecords()));

                if(IS_DEBUG) {
                    for (CloudFavoriteContactInfo item : mCloudFavorites) {
                        MktLog.d(TAG, item.toString());
                    }
                }

                String oldFavoriteETag = CurrentUserSettings.getSettings().getCurrentFavoriteETag();
                String newFavoriteETag = null;

                //modified e-tag
                HashMap<String, String> responseHeader = request.getResponseHeader();
                if(responseHeader.containsKey(HEADER_E_TAG)) {
                    newFavoriteETag = responseHeader.get(HEADER_E_TAG);
                }

                if(!TextUtils.isEmpty(newFavoriteETag)) {
                    newFavoriteETag = newFavoriteETag.replace("\"", "");
                    CurrentUserSettings.getSettings().setCurrentFavoriteETag(newFavoriteETag);
                    EngLog.d(TAG, "doRequest,finish, e-tag=" + newFavoriteETag);
                }

                if(!TextUtils.isEmpty(newFavoriteETag)) {
                    if(newFavoriteETag.equals(oldFavoriteETag)) {
                        if(mListener != null) {
                            mListener.onRequestFailure(RestApiErrorCodes.SC_NOT_MODIFIED_304);
                        }
                    }else {
                        if (mListener != null) {
                            mListener.onRequestSuccess();
                        }
                    }
                }else {
                    EngLog.d(TAG, "doRequest, finish, e-tag= null, not call request success.");
                }

                synchronized (this) {
                    mExecutionFinished = true;
                }

            }

            @Override
            public void onFail(RcRestRequest<RestListResponse<CloudFavoriteContactInfo>> request, int errorCode) {
                if (LogSettings.MARKET) {
                    MktLog.e(TAG, "onFail errorCode=" + errorCode);
                }

                if (mListener != null) {

//                    if(request.getHttpCode() == RestApiErrorCodes.SC_NOT_MODIFIED_304) {
//                        EngLog.d(TAG, "doRequest,onFail, httpCode=" + RestApiErrorCodes.SC_NOT_MODIFIED_304);
//                        mListener.onRequestFailure(RestApiErrorCodes.SC_NOT_MODIFIED_304);
//                    }else {
//                        mListener.onRequestFailure(errorCode);
//                    }
                }

                synchronized (this) {
                    mExecutionFinished = true;
                }
            }

            @Override
            public void onComplete(RcRestRequest<RestListResponse<CloudFavoriteContactInfo>> request) {
                if (LogSettings.MARKET) {
                    MktLog.i(TAG, "onComplete");
                }

            }
        });
        MktLog.i(TAG, "doRequest");
        request.executeRequest(context);
    }
}
