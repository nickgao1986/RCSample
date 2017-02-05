package com.example.nickgao.contacts.adapters.contactsprovider;

import android.content.Context;

import com.example.nickgao.contacts.CloudPersonalContactsServiceAbstract;
import com.example.nickgao.network.RestApiErrorCodes;
import com.example.nickgao.network.RestSession;
import com.example.nickgao.service.RestRequestListener;

/**
 * Created by nick.gao on 1/30/17.
 */

public class CloudPersonalContactsServiceLoader {

    private static final String TAG = "[RC]PersonalContactsLoader";

    private static CloudPersonalContactsServiceLoader sLoader;

    private CloudPersonalContactsServiceAbstract mLoader;

    public static synchronized CloudPersonalContactsServiceLoader getLoader(){
        if (sLoader == null){
            sLoader = new CloudPersonalContactsServiceLoader();
        }

        return sLoader;
    }

    private CloudPersonalContactsServiceLoader(){

    }

    public boolean updatePersonalContacts(Context context, RestRequestListener listener) {
        if (!RestSession.validateSession(context)){
            //make sure listener invoked.
            if(listener != null) {
                listener.onRequestFailure(RestApiErrorCodes.SC_UNAUTHORIZED_401);
            }
            return false;
        }

        //TODO may be add check for freezed requests
        if (mLoader != null && !mLoader.executionFinished()){
            return true;
        }

        //FIXME add date check
//        final String syncToken = CurrentUserSettings.getSettings().getPersonalContactsSyncToken();
//        String className = TextUtils.isEmpty(syncToken)
//                ? CloudPersonalContactsServiceFSync.class.getName()
//                : CloudPersonalContactsServiceISync.class.getName();
//
//        mLoader = (CloudPersonalContactsServiceAbstract) ServiceFactory.getInstance().getService(className);
//        mLoader.setListener(listener);
//        mLoader.updateContacts(context);

        return true;
    }

}
