package com.example.nickgao.service.clientinfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.service.AbstractService;
import com.example.nickgao.service.IRequestFactory;
import com.example.nickgao.service.RcRestRequest;
import com.example.nickgao.service.response.ClientInfoResponse;
import com.example.nickgao.service.response.*;

/**
 * Created by nick.gao on 2014/7/16.
 */
public class ClientInfoService extends AbstractService {

    public static final String TAG = "[RC]ClientInfoService";
    public static final int TPYE_FOR_MOBILE_WEBUSER_SETTINGS = 30;
    public static final int TPYE_FOR_PHONE_SYSTEM = 31;
    public static final int TYPE_FOR_INTERNATIONAL_CALLING = 32;
    public static final int TYPE_BILLING = 33;
    public static final int TPYE_RCFAX_FOR_MOBILE_WEBUSER_SETTINGS = 34;

    public static final int TYPE_FOR_EXPRESS_SETUP = 36;

    public static final int TPYE_FOR_TELL_FRIEND = 35;
    public static final int TPYE_FOR_RESET_PASSWORD = 36;
    public static final int TPYE_FOR_REPORT = 37;
    public ClientInfoService(IRequestFactory requestFactory) {
        super(requestFactory);
    }

    public void updateClientInfo() {
        doRequest();
    }

    private void doRequest() {
        final Context context = RingCentralApp.getContextRC();
        RcRestRequest<ClientInfoResponse> request = this.mRequestFactory.createClientInfoRequest();
        request.registerOnRequestListener(new RcRestRequest.OnRequestListener<ClientInfoResponse>() {
            @Override
            public void onSuccess(RcRestRequest<ClientInfoResponse> request, ClientInfoResponse mResponseData) {
                if(mResponseData.getProvisioning() != null) {
                    Hints hints = mResponseData.getProvisioning().getHints();
                    WebUris webUri = mResponseData.getProvisioning().getWebUris();
                    MktLog.i(TAG, "===webUri="+webUri.getMobileWebPhoneSystem());
                }

                if (mListener != null) {
                    mListener.onRequestSuccess();
                }

            }

            @Override
            public void onFail(RcRestRequest<ClientInfoResponse> request, int errorCode) {
                if (mListener != null) {
                    mListener.onRequestFailure(errorCode);
                }

            }

            @Override
            public void onComplete(RcRestRequest<ClientInfoResponse> request) {

            }
        });
        MktLog.i(TAG, "client info service request");
        request.executeRequest(context);
    }


    public void unRegisterListener() {
        mListener = null;
    }


}
