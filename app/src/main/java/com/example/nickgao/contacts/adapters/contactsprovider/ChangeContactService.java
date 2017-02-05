package com.example.nickgao.contacts.adapters.contactsprovider;

/**
 * Created by nick.gao on 1/30/17.
 */

import android.content.Context;

import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.service.AbstractService;
import com.example.nickgao.service.IRequestFactory;
import com.example.nickgao.service.request.RcRestRequest;


/**
 * Created by kasni.huang on 2015/01/27.
 */
public class ChangeContactService extends AbstractService {
    public static final String TAG = "[RC]ChangeContactService";
    public static final boolean DEBUG = false;
    private ContactSyncListener mContactSyncListener = null;
    public ChangeContactService(IRequestFactory requestFactory) {
        super(requestFactory);
    }

    public void updateData(int requestType, long id, String requestBody) {
        doRequest(requestType, id, requestBody);
    }

    public interface ContactSyncListener {
        void onContactSyncSuccess(int requestType, long contactId, CloudPersonalContactInfo response);
        void onContactSyncFailure(int requestType, long contactId, RcRestRequest<CloudPersonalContactInfo> request, int errorCode);
    }

    public void setContactSyncListener(ContactSyncListener listener) {
        mContactSyncListener = listener;
    }

    private void doRequest(final int requestType, final long id, String requestBody) {
        final Context context = RingCentralApp.getContextRC();
        RcRestRequest<CloudPersonalContactInfo> request = getRequest(requestType, requestBody);
        request.registerOnRequestListener(new RcRestRequest.OnRequestListener<CloudPersonalContactInfo>() {
            @Override
            public void onSuccess(RcRestRequest<CloudPersonalContactInfo> request, CloudPersonalContactInfo response) {
                MktLog.i(TAG, "CreateSingleContactService onSuccess");
                if (mContactSyncListener != null) {
                    mContactSyncListener.onContactSyncSuccess(requestType, id, response);
                }
            }

            @Override
            public void onFail(RcRestRequest<CloudPersonalContactInfo> request, int errorCode) {
                if (LogSettings.MARKET) {
                    MktLog.e(TAG, "onFail createSingleContact errorCode=" + errorCode);
                }
                if (mContactSyncListener != null) {
                    mContactSyncListener.onContactSyncFailure(requestType, id, request, errorCode);
                }
            }

            @Override
            public void onComplete(RcRestRequest<CloudPersonalContactInfo> request) {
                if (LogSettings.MARKET) {
                    MktLog.i(TAG, "on createSingleContact onComplete send broadcast");
                }

            }
        });
        MktLog.i(TAG, "create single contact service request");
        if (requestType == RequestInfoStorage.REST_CREATE_SINGLE_CONTACT) {
            request.executeRequest(context);
        } else {
            request.executeRequest(context, id);
        }
    }

    public RcRestRequest<CloudPersonalContactInfo> getRequest(int requestType, String requestBody) {
        RcRestRequest<CloudPersonalContactInfo> request;
        switch (requestType) {
            case RequestInfoStorage.REST_CREATE_SINGLE_CONTACT:
                request = this.mRequestFactory.createSingleContactRequest(requestBody);
                break;
            case RequestInfoStorage.REST_UPDATE_CONTACT:
                request = this.mRequestFactory.updateContactRequest(requestBody);
                break;
            case RequestInfoStorage.REST_DELETE_CONTACT:
                request = this.mRequestFactory.deleteContactRequest();
                break;
            default:
                request = this.mRequestFactory.createSingleContactRequest(requestBody);
        }
        return request;
    }
}
