package com.example.nickgao.service;

/**
 * Created by steve.chen on 6/30/14.
 */
public abstract class AbstractService {

    protected String TAG;

    protected IRequestFactory mRequestFactory;
    protected RestRequestListener mListener;

    public void setListener(RestRequestListener listener) {
        this.mListener = listener;
    }

    public AbstractService(IRequestFactory requestFactory) {
        TAG = this.getClass().getSimpleName();
        mRequestFactory = requestFactory;
    }

//    protected boolean sentRequest(AbstractRequest<?> request, Context context) {
//
//        boolean result = false;
//
//        RestSession session = RestSession.get(RCMProviderHelper.getCurrentMailboxId(context));
//        if (session != null) {
//            result = session.sendRequest(request);
//            if (!result) {
//                int errorCode = request.getResult();
//                MktLog.e(TAG, request.getPath() + " failed: error " + errorCode + ": " + RestApiErrorCodes.getMsg(errorCode));
//            }
//        } else {
//            MktLog.e(TAG, request.getPath() + " failed: error " + RestApiErrorCodes.INVALID_SESSION_STATE + " Invalid Session State");
//        }
//
//        return result;
//    }
}
