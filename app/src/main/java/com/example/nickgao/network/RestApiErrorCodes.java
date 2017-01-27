/**
 * Copyright (C) 2012, RingCentral, Inc.
 * All Rights Reserved.
 */
package com.example.nickgao.network;

import org.apache.http.HttpStatus;

import android.util.SparseArray;

/**
 * Constants enumerating the status codes used across REST API implementation: 
 * only negative values and "zero" that means OK. 
 */
public final class RestApiErrorCodes {
    /**
     * Generic success code.
     */
    public static final int OK       = 0;
    public static final int NO_ERROR = 0;

    /**
     * Used for identification of network unavailability to make a request. 
     */
    public static final int NETWORK_NOT_AVAILABLE = -1;
    
    /**
     * Defines state when any network operations are forbidden due to airplane mode switched on the device.
     */
    public static final int NETWORK_NOT_AVAILABLE_AIRPLANE_ON = -2;

    /**
     * Defines invalid request.
     */
    public static final int INVALID_REQUEST = -201;
    
    /**
     * Defines invalid REST Session state.
     */
    public static final int INVALID_SESSION_STATE = -202;
    
    /**
     * Defines invalid REST Session state.
     */
    public static final int RESPONSE_INVALID_FORMAT = -203;
    
    /**
     * Defines status code when HTTP Response status code is not 200 OK.
     */
    public static final int RESPONSE_HTTP_STATUS_ERROR = -204;
    
    /**
     * Defines status code when an error happened during HTTP response processing.
     */
    public static final int RESPONSE_PROCESSING_ERROR = -205;
    
    /**
     * Defines status code when HTTP connection establishing failed
     */
    public static final int CONNECTION_ERROR = -206;
    
    /**
     * Defines status code when an authorization failed
     */
    public static final int AUTHORIZATION_ERROR = -207;
    
    /**
     * Defines status code when attachment is too large
     */
    public static final int OUT_OF_MEMORY_ERROR = -208;
    
    /**
     * Defines status code when reply department failed
     */
    public static final int REST_DEPARTMENT_ERROR = -209;
    
    /**
     * Messaging API error codes
     */
    public static final int INVALID_MESSAGE_DRAFT = -301;
    public static final int INVALID_RECIPIENT_NUMBER = -302;
    public static final int INVALID_SENDER_NUMBER = -303;
    public static final int INVALID_MESSAGE_TYPE = -304;
    public static final int INVALID_CONVERSATION_ID = -305;
    public static final int MESSAGE_ISYNC_FAILED = -306;
    public static final int INVALID_MESSAGE_ID = -307;
    public static final int MESSAGE_DELETED = -308;
    public static final int MESSAGE_LOADING = -309;
    public static final int MESSAGE_LOADED = -310;
    public static final int NO_ATTACHMENT = -311;
    public static final int INVALID_CONTENT_LENGTH = -312;
    public static final int NO_CONTENT_DISPOSITION = -313;
    public static final int NO_FILENAME = -314;
    public static final int FILE_WRITE_ERROR = -315;
    public static final int INVALID_PARAMETER = -316;

    public static final int REST_UNKNOWN_LOGICAL_ERROR = -400;
    public static final int REST_InvalidParameter = -401;
    public static final int REST_InternationalProhibited = -402;
    public static final int REST_InsufficientFunds = -403;
    public static final int REST_InvalidContent = -404;
    public static final int REST_FeatureNotAvailable = -405;
    public static final int REST_GatewayRejected = -406;
    public static final int REST_RejectedByRecipient = -407;
    public static final int REST_RecipientDoesntSupportMessage = -408;
    public static final int REST_OldThreadReply = -409;
    public static final int REST_OutOfThreadReply = -410;
    
    /**
     * Faxout API error codes
     */
	public static final int NO_TO_PHONE  					= -101;
	public static final int APP_EXIT  						= -102;
	
	/**
     * Call Log API error codes
     */
	public static final int INVALID_CALL_LOG_STATUS_GROUP 			= -1304;
	public static final int CALL_LOG_ISYNC_FAILED 					= -1306;
	public static final int CALL_LOG_SYNC_MISSED_MAXIMUM_FAILED 	= -1307;


    /**
     * Used for cases when something failed in implementation.
     *
     */
    public static final int INTERNAL_ERROR_CODE = -500;
    
    /**
     * Used for cases when the result is not defined yet.
     */
    public static final int UNKNOWN_STATUS_CODE = -999;
    
    
    /*******************************OAUTH CODES***********************************************/
    /**
    http://wiki.ringcentral.com/display/ENG/API+Client+Error+Handling+Guidelines
    */
    public static final int SC_OK_200 = HttpStatus.SC_OK;;
    public static final int SC_CREATED_201 = HttpStatus.SC_CREATED;
    public static final int SC_NO_CONTENT_204 = HttpStatus.SC_NO_CONTENT;
    public static final int SC_MULTI_STATUS_207 = HttpStatus.SC_MULTI_STATUS;
    
    /**
     * Bad Request,invalid_grant,do not repeat request
     */
    public static final int SC_BAD_REQUEST_400 = HttpStatus.SC_BAD_REQUEST;
    
    
    /**
     * Request Timeout,Repeat the request at a later time after some delay (limited number of attempts).
     * If still failing, display error message.
     */
    public static final int SC_REQUEST_TIMEOUT_408 = HttpStatus.SC_REQUEST_TIMEOUT;
    
    /**Too Many Requests,Can occur due to incorrect application logic.
     * Repeat the request at a later time after some delay (limited number of attempts). 
     * The reasonable delay may vary depending on request type.If still failing, display error message.
     */
    public static final int SC_MULTIPLE_REQUEST_429 = 429;
    
    //public static final int OAUTH_RESPONSE_CODE_4XX
    /**
     * Internal Server Error,Unexpected error in server code.
     * Repeat the request (limited number of attempts). If still failing, display error message.
     */
    public static final int SC_INTERNAL_SERVER_ERROR_500 = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    
    /**
     * Bad Gateway,The service is temporary unavailable.
     * Repeat the request at a later time after some delay (limited number of attempts). If still failing, display error message.
     */
    public static final int SC_BAD_GATEWAY_502 = HttpStatus.SC_BAD_GATEWAY;
    
    /**
     * Service Unavailable,The service is temporary unavailable.
     * Repeat the request at a later time after some delay (limited number of attempts). If still failing, display error message.
     */
    public static final int SC_SERVICE_UNAVAILABLE_503 = HttpStatus.SC_SERVICE_UNAVAILABLE;
    
    /**
     * BGateway Timeout,The service is temporary unavailable.
     * Repeat the request at a later time after some delay (limited number of attempts). If still failing, display error message.
     */
    public static final int SC_GATEWAY_TIMEOUT_504 = HttpStatus.SC_GATEWAY_TIMEOUT;
    
    //public static final int PLATFORM_RESPONSE_CODE_5XX
    public static final int CLIENT_INVALID_ERROR = 999;
    
    
    /****************************************REQULAR CODES****************************************************/
    /**
     * Unauthorized,Error code:TokenInvalid (5.12+) DO NOT repeat the request. Perform OAuth authentication to get new tokens.
     * Error code:TokenExpired (5.12+),Access token is expired.Perform token refreshment, then repeat request with new access token.
     */
    public static final int SC_UNAUTHORIZED_401 = HttpStatus.SC_UNAUTHORIZED;
    
    /**
     * Forbidden,DO NOT repeat the request.
     */
    public static final int SC_FORBIDDEN_403 = HttpStatus.SC_FORBIDDEN;
    
    /**
     * Not Found,The requested resource is not found.DO NOT repeat the request.
     */
    public static final int SC_NOT_FOUND_404 = HttpStatus.SC_NOT_FOUND;
    
    /**
     * Keeps messages definition of status codes.
     */
    private static SparseArray<String> sMsgCodes = new SparseArray<String>();

    /**
     * Static initializer for enumeration messages definition.
     */
    static {
    	sMsgCodes.put(SC_OK_200, SC_OK_200 + " OK (NO ERROR)");
    	sMsgCodes.put(SC_CREATED_201, SC_CREATED_201 + " OK (NO ERROR)");
    	sMsgCodes.put(SC_NO_CONTENT_204, SC_NO_CONTENT_204 + " OK (NO ERROR)");
    	sMsgCodes.put(SC_MULTI_STATUS_207, SC_MULTI_STATUS_207 + " OK (NO ERROR)");
    	sMsgCodes.put(SC_BAD_REQUEST_400, SC_BAD_REQUEST_400 + " Bad Request,invalid_grant,do not repeat request");
    	sMsgCodes.put(SC_UNAUTHORIZED_401, SC_UNAUTHORIZED_401 + " Unauthorized,do not repeat request");
    	sMsgCodes.put(SC_FORBIDDEN_403, SC_FORBIDDEN_403 + " Forbidden,do not repeat the request");
    	sMsgCodes.put(SC_NOT_FOUND_404, SC_NOT_FOUND_404 + " Not Found,The requested resource is not found.DO NOT repeat the request");
    	sMsgCodes.put(SC_REQUEST_TIMEOUT_408, SC_REQUEST_TIMEOUT_408 + " Request Timeout,Repeat the request later");
    	sMsgCodes.put(SC_MULTIPLE_REQUEST_429, SC_MULTIPLE_REQUEST_429 + " Too Many Requests,Repeat the request later");
    	sMsgCodes.put(SC_INTERNAL_SERVER_ERROR_500, SC_INTERNAL_SERVER_ERROR_500 + " Internal Server Error,Unexpected error in server code,Repeat the request later");
    	sMsgCodes.put(SC_BAD_GATEWAY_502, SC_BAD_GATEWAY_502 + " Bad Gateway,The service is temporary unavailable,Repeat the request later");
    	sMsgCodes.put(SC_SERVICE_UNAVAILABLE_503, SC_SERVICE_UNAVAILABLE_503 + " Service Unavailable,The service is temporary unavailable,Repeat the request later");
    	sMsgCodes.put(SC_GATEWAY_TIMEOUT_504, SC_GATEWAY_TIMEOUT_504 + " BGateway Timeout,The service is temporary unavailable,Repeat the request later");
    	
    	
        sMsgCodes.put(OK, OK + " OK (NO ERROR)");
        sMsgCodes.put(NETWORK_NOT_AVAILABLE,              NETWORK_NOT_AVAILABLE              + " Network Not Available");
        sMsgCodes.put(NETWORK_NOT_AVAILABLE_AIRPLANE_ON,  NETWORK_NOT_AVAILABLE_AIRPLANE_ON  + " Airplane Mode");
        
        
        sMsgCodes.put(INVALID_REQUEST,               INVALID_REQUEST            + " Invalid Request");
        sMsgCodes.put(INVALID_SESSION_STATE,         INVALID_SESSION_STATE      + " Invalid Session State");
        
        sMsgCodes.put(RESPONSE_INVALID_FORMAT,       RESPONSE_INVALID_FORMAT    + " Response Invalid Format");
        sMsgCodes.put(RESPONSE_HTTP_STATUS_ERROR,    RESPONSE_HTTP_STATUS_ERROR + " HTTP Status Error");
        sMsgCodes.put(RESPONSE_PROCESSING_ERROR,     RESPONSE_PROCESSING_ERROR  + " HTTP Response Processing Failed");
        
        sMsgCodes.put(INTERNAL_ERROR_CODE,           INTERNAL_ERROR_CODE        + " Implementation internal error");
        
        sMsgCodes.put(CONNECTION_ERROR,              CONNECTION_ERROR           + " HTTP Connection Establishing Failed");
        
        sMsgCodes.put(AUTHORIZATION_ERROR,           AUTHORIZATION_ERROR        + " Authorization Failed");
        
        
        sMsgCodes.put(INVALID_MESSAGE_DRAFT,         INVALID_MESSAGE_DRAFT      + " Invalid message draft");
        sMsgCodes.put(INVALID_RECIPIENT_NUMBER,      INVALID_RECIPIENT_NUMBER   + " Invalid recipient number");
        sMsgCodes.put(INVALID_SENDER_NUMBER,         INVALID_SENDER_NUMBER      + " Invalid sender's number");
        sMsgCodes.put(INVALID_MESSAGE_TYPE,          INVALID_MESSAGE_TYPE       + " Invalid message type");
        sMsgCodes.put(INVALID_CONVERSATION_ID,       INVALID_CONVERSATION_ID    + " Invalid conversation ID");
        sMsgCodes.put(MESSAGE_ISYNC_FAILED,          MESSAGE_ISYNC_FAILED       + " ISync failed: FSync required.");
        sMsgCodes.put(INVALID_MESSAGE_ID,            INVALID_MESSAGE_ID         + " Invalid message ID");
        sMsgCodes.put(MESSAGE_DELETED,               MESSAGE_DELETED            + " Message deleted");
        sMsgCodes.put(MESSAGE_LOADING,               MESSAGE_LOADING            + " Message is loading");
        sMsgCodes.put(MESSAGE_LOADED,                MESSAGE_LOADED             + " Message already loaded");
        sMsgCodes.put(NO_ATTACHMENT,                 NO_ATTACHMENT              + " Message has no attachment");
        sMsgCodes.put(INVALID_CONTENT_LENGTH,        INVALID_CONTENT_LENGTH     + " Invalid content length");
        sMsgCodes.put(NO_CONTENT_DISPOSITION,        NO_CONTENT_DISPOSITION     + " No Content-Disposition header");
        sMsgCodes.put(NO_FILENAME,                   NO_FILENAME                + " No filename or empty filename element in Content-Disposition");
        sMsgCodes.put(FILE_WRITE_ERROR,              FILE_WRITE_ERROR           + " File write error");
        sMsgCodes.put(INVALID_PARAMETER,             INVALID_PARAMETER          + " Invalid Parameter");

        sMsgCodes.put(REST_UNKNOWN_LOGICAL_ERROR,           REST_UNKNOWN_LOGICAL_ERROR          + " REST error: Unknown logical error");
        sMsgCodes.put(REST_InvalidParameter,                REST_InvalidParameter               + " REST error: InvalidParameter");
        sMsgCodes.put(REST_InternationalProhibited,         REST_InternationalProhibited        + " REST error: InternationalProhibited");
        sMsgCodes.put(REST_InsufficientFunds,               REST_InsufficientFunds              + " REST error: InsufficientFunds");
        sMsgCodes.put(REST_InvalidContent,                  REST_InvalidContent                 + " REST error: InvalidContent");
        sMsgCodes.put(REST_FeatureNotAvailable,             REST_FeatureNotAvailable            + " REST error: FeatureNotAvailable");
        sMsgCodes.put(REST_GatewayRejected,                 REST_GatewayRejected                + " REST error: GatewayRejected");
        sMsgCodes.put(REST_RejectedByRecipient,             REST_RejectedByRecipient            + " REST error: RejectedByRecipient");
        sMsgCodes.put(REST_RecipientDoesntSupportMessage,   REST_RecipientDoesntSupportMessage  + " REST error: RecipientDoesntSupportMessage");
        sMsgCodes.put(REST_OldThreadReply,   				REST_OldThreadReply  				+ " REST error: OldThreadReply");
        sMsgCodes.put(REST_OutOfThreadReply,   				REST_OutOfThreadReply  				+ " REST error: OutOfThreadReply");
        
        sMsgCodes.put(UNKNOWN_STATUS_CODE,           UNKNOWN_STATUS_CODE        + " Unknown Status code");
    }

    /**
     * for 400 error code
     */
    public static String ERROR_CODE_INVALID_GRANT = "invalid_grant";
    public static String ERROR_CODE_INVALID_REQUEST = "invalid_request";
    public static String ERROR_CODE_INVALID_CLIENT = "invalid_client";
    public static String ERROR_CODE_UNAUTHORIZED_CLIENT = "unauthorized_client";
    public static String ERROR_CODE_TOKEN_INVALLID = "TokenInvalid";
    public static String ERROR_CODE_TOKEN_EXPIRED = "TokenExpired";
    /**
     * Returns a user-friendly message described the status code for using in logging.
     *  
     * @param statusCode the status code
     * 
     * @return a message described the status code
     */
    public static String getMsg(int statusCode) {
        String msg = sMsgCodes.get(statusCode);
        if (msg == null) {
            return new String(statusCode + "(UNKNOWN STATUS CODE)");
        } else {
            return msg;
        }
    }
}
