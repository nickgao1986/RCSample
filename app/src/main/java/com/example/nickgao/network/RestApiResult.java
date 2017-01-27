/**
 * Copyright (C) 2012, RingCentral, Inc.
 * All Rights Reserved.
 */
package com.example.nickgao.network;

/**
 * Return value type for the asynchronous MsgAPI operations.
 */
public class RestApiResult {

    /*
     * Intent EXTRA's for the broadcast asynchronous operation results
     */
    public static final String EXTRA_MSGAPI_REQUEST_ID 				= "com.ringcentral.android.intent.extra.EXTRA_MSGAPI_REQUEST_ID";
    public static final String EXTRA_MSGAPI_SUCCESS 				= "com.ringcentral.android.intent.extra.EXTRA_MSGAPI_SUCCESS";
    public static final String EXTRA_MSGAPI_ERROR_CODE 				= "com.ringcentral.android.intent.extra.EXTRA_MSGAPI_ERROR_CODE";
    public static final String EXTRA_MESSAGE_ID 					= "com.ringcentral.android.intent.extra.EXTRA_MESSAGE_ID";
    public static final String EXTRA_SENDMESSAGE_CONVERSATION_ID 	= "com.ringcentral.android.intent.extra.CONVERSATION_ID";
    public static final String EXTRA_MESSAGE_LIST_TYPE 				= "com.ringcentral.android.intent.extra.EXTRA_MESSAGE_LIST_TYPE";
    public static final String EXTRA_MSGAPI_DELETE_MSG 				= "com.ringcentral.android.intent.extra.EXTRA_MSGAPI_DELETE_MSG";
    public static final String EXTRA_MSGAPI_UPDATE_MSG 				= "com.ringcentral.android.intent.extra.EXTRA_MSGAPI_UPDATE_MSG";
    public static final String EXTRA_MSGAPI_DRAFT_URI               = "com.ringcentral.android.intent.extra.EXTRA_MSGAPI_DRAFT_URI";
    public static final String EXTRA_MSGAPI_REQUEST_URI             = "com.ringcentral.android.intent.extra.EXTRA_MSGAPI_REQUEST_URI";
    
    public static final String EXTRA_CALL_LOG_STATUS_GROUP 		= "com.ringcentral.android.intent.extra.EXTRA_CALL_LOG_STATUS_GROUP";
    public static final String EXTRA_CALL_LOG_API_SUCCESS 		= "com.ringcentral.android.intent.extra.EXTRA_CALL_LOG_SUCCESS";
    public static final String EXTRA_CALL_LOG_API_ERROR_CODE 	= "com.ringcentral.android.intent.extra.EXTRA_CALL_LOG_API_ERROR_CODE";


    /**
     * true:    Indicates that the asynchronous (network) operation has been invoked successfully.
     *          This does not guarantee that the actual server request will be perfomed successfully;
     *          this only indicates that the Messaging Framework has sent a request to the server.
     *          The connection may fail, or the server itself may return an error.
     *          In this case the operation result and error code will be provided asynchronously.
     * false:   Indicates that the server request could not be sent due to an error
     *          (no network, incorrect parameters, etc.)
     */
    public boolean success;

    /**
     * Identifier of the asynchronous request;
     * the same id will be returned in the EXTRA_MSGAPI_REQUEST_ID of the broadcast Intent for this operation.
     * This field is meaningful only when success==true.
     */
    public long requestId;

    /**
     * Error code.
     * This field is meaningful only when success==false.
     */
    public int errorCode;


    /**
     * Identifies that message list request returned all message types
     * instead of the requested message type only
     * (this happens in case of the old MessageSync APi which always returns complete message list)
     *
     * This field is meaningful only for the message list requests (getMessageList, syncMessageList, expandMessageList)
     */
    public boolean listAllTypes;


    public RestApiResult(boolean success, long requestId, int errorCode, boolean msgListAllTypes) {
        this.success = success;
        this.requestId = requestId;
        this.errorCode = errorCode;
        this.listAllTypes = msgListAllTypes;
    }

    public RestApiResult(boolean success, long requestId, int errorCode) {
        this(success, requestId, errorCode, false);
    }

    public RestApiResult(long requestId) {
        this(true, requestId, RestApiErrorCodes.NO_ERROR);
    }

    public RestApiResult(int errorCode) {
        this(false, -1, errorCode);
    }

    public RestApiResult(long requestId, boolean listAllTypes) {
        this(true, requestId, RestApiErrorCodes.NO_ERROR, listAllTypes);
    }

    public RestApiResult(int errorCode, boolean listAllTypes) {
        this(false, -1, errorCode, listAllTypes);
    }

}
