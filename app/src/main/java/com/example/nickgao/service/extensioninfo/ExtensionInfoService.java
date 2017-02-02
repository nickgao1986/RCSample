package com.example.nickgao.service.extensioninfo;

import android.content.Context;
import android.content.Intent;

import com.example.nickgao.BuildConfig;
import com.example.nickgao.datastore.extensioninfo.ExtensionInfoDataStore;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.service.AbstractService;
import com.example.nickgao.service.IRequestFactory;
import com.example.nickgao.service.model.extensioninfo.ExtensionInfoResponse;
import com.example.nickgao.service.request.RcRestRequest;
import com.example.nickgao.utils.RCMConstants;

/**
 * Created by nick.gao on 2/1/17.
 */

public class ExtensionInfoService extends AbstractService {
    public static final String TAG = "[RC]ExtensionInfoService";
    public static final boolean DEBUG = false;
    public static final String REST_SERVICE_INFO_COMPLETION_NOTIFICATION = BuildConfig.APPLICATION_ID + ".intent.action.REST_SERVICE_INFO_COMPLETION_NOTIFICATION";

    public ExtensionInfoService(IRequestFactory requestFactory) {
        super(requestFactory);
    }

    public void updateData() {
        doRequest();
    }

    private void doRequest() {
        final Context context = RingCentralApp.getContextRC();
        RcRestRequest<ExtensionInfoResponse> request = this.mRequestFactory.createGetExtensionInfoRequest();
        request.registerOnRequestListener(new RcRestRequest.OnRequestListener<ExtensionInfoResponse>() {
            @Override
            public void onSuccess(RcRestRequest<ExtensionInfoResponse> request, ExtensionInfoResponse mResponseData) {
                MktLog.i(TAG, "ExtensionInfoService onsuccess");

                ExtensionInfoDataStore.storeExtensionInfo(mResponseData);


                if (mListener != null) {
                    //only in login mListener != null
                    mListener.onRequestSuccess();
                } else {
                    notifyServiceInfoRequestResult();
                }
            }

            @Override
            public void onFail(RcRestRequest<ExtensionInfoResponse> request, int errorCode) {
                if (LogSettings.MARKET) {
                    MktLog.e(TAG, "onFail get GetExtensionInfo errorCode=" + errorCode);
                }

                if (mListener != null) {
                    mListener.onRequestFailure(errorCode);
                }
            }

            @Override
            public void onComplete(RcRestRequest<ExtensionInfoResponse> request) {
                if (LogSettings.MARKET) {
                    MktLog.i(TAG, "on get GetExtensionInfo onComplete send broadcast");
                }

            }
        });
        MktLog.i(TAG, "extension info service request");
        request.executeRequest(context);
    }

    private void notifyServiceInfoRequestResult() {
        Intent intent = new Intent(REST_SERVICE_INFO_COMPLETION_NOTIFICATION);
        RingCentralApp.getContextRC().sendBroadcast(intent);

        intent = new Intent(RCMConstants.ACTION_CHECK_LANGUAGE);
        RingCentralApp.getContextRC().sendBroadcast(intent);
    }


}
