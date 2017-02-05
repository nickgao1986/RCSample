package com.example.nickgao.contacts;

import android.content.Context;
import android.content.Intent;

import com.example.nickgao.BuildConfig;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContactInfo;
import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.service.AbstractService;
import com.example.nickgao.service.IRequestFactory;
import com.example.nickgao.service.request.RcRestRequest;
import com.example.nickgao.utils.RCMConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nick.gao on 1/30/17.
 */

public abstract class CloudPersonalContactsServiceAbstract extends AbstractService {


    public final static String PERSONAL_CONTACTS_UPDATE_COMPLETED =
            BuildConfig.APPLICATION_ID + ".PERSONAL_CONTACTS_UPDATE_COMPLETED";
    public static final String PERSONAL_CONTACTS_UPDATE_SUCCESS = "update_success";
    public static final String PERSONAL_CONTACTS_UPDATE_FAILED_REASON = "update_failed_reason";

    protected List<CloudPersonalContactInfo> mContacts;

    private volatile boolean mExecutionFinished = false;

    protected abstract void processResult(Context context, List<CloudPersonalContactInfo> contacts);

    protected abstract void onFailed(RcRestRequest<CloudPersonalContactsSyncResponse> request,int errorCode);

    protected abstract CloudPersonalContactsSyncRequest createRequest();

    public CloudPersonalContactsServiceAbstract(IRequestFactory requestFactory) {
        super(requestFactory);
    }


    public void updateContacts(Context context) {
        MktLog.d(TAG, "update contacts");

        if (mContacts != null){
            throw new IllegalStateException();
        }
        mContacts = new ArrayList<>();
        doRequest(RCMConstants.PAGE_START_INDEX, context);
    }

    public boolean executionFinished() {
        return mExecutionFinished;
    }

    private void doRequest(int page, final Context context) {
        CloudPersonalContactsSyncRequest request =  createRequest();

        request.registerOnRequestListener(new RcRestRequest.OnRequestListener<CloudPersonalContactsSyncResponse>() {

            @Override
            public void onSuccess(RcRestRequest<CloudPersonalContactsSyncResponse> request, CloudPersonalContactsSyncResponse response) {
                mContacts.addAll(Arrays.asList(response.getRecords()));
                CloudPersonalContactsSyncRequest pageRequest = (CloudPersonalContactsSyncRequest) request;

                if (pageRequest.hasMore()) {
                    MktLog.i(TAG, "loading next page");
                    pageRequest.createNextPageRequest().executeRequest(context);
                } else {
                    mExecutionFinished = true;
                    //saveTimeUpdated(System.currentTimeMillis());
                    saveToken(response.getSyncInfo().syncToken);
                    processResult(context, mContacts);
                    //update status, todo?
                    updateSuccess(context);
                    if(mListener != null) {
                        mListener.onRequestSuccess();
                    }
                }
            }

            @Override
            public void onFail(RcRestRequest<CloudPersonalContactsSyncResponse> request, int errorCode) {
                mExecutionFinished = true;
                onFailed(request, errorCode);
                //saveTimeUpdated(0L);
                //update status, todo
                updateFailed(context, errorCode);

                if(mListener != null) {
                    mListener.onRequestFailure(errorCode);
                }

            }

            @Override
            public void onComplete(RcRestRequest<CloudPersonalContactsSyncResponse> request) {

            }
        });

        request.setPage(page);
        MktLog.i(TAG, "personal contacts " + request.getClass().getSimpleName() + "request");
        request.executeRequest(context);
    }

    protected void saveToken(String token){
        CurrentUserSettings.getSettings().setPersonalContactsSyncToken(token);
    }

    /* //there is no requirement
    private void saveTimeUpdated(long timeMs){
        CurrentUserSettings.getSettings().setPersonalContactsLastUpdateTime(timeMs);
    }*/

    private void updateSuccess(Context context) {
        Intent intent = new Intent(PERSONAL_CONTACTS_UPDATE_COMPLETED);
        intent.putExtra(PERSONAL_CONTACTS_UPDATE_SUCCESS, true);
        context.sendBroadcast(intent, RCMConstants.INTERNAL_PERMISSION_NAME);
    }

    private void updateFailed(Context context, int reason) {
        Intent intent = new Intent(PERSONAL_CONTACTS_UPDATE_COMPLETED);
        intent.putExtra(PERSONAL_CONTACTS_UPDATE_SUCCESS, false);
        intent.putExtra(PERSONAL_CONTACTS_UPDATE_FAILED_REASON, reason);
        context.sendBroadcast(intent, RCMConstants.INTERNAL_PERMISSION_NAME);
    }
}
