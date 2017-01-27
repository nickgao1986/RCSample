/** 
 * Copyright (C) 2012, RingCentral, Inc.
 *  
 * All Rights Reserved.
 */
package com.example.nickgao.network;

import java.io.Reader;

import android.text.TextUtils;

import com.google.agson.stream.JsonReader;
import com.example.nickgao.logging.MktLog;

/**
 * Processing Platform API Error Response 
 */
public class RestErrorResponse {
    public enum Codes {
        /**
         * For Pager messages only. The recipient phone number cannot accept text messages.
         */
    	RecipientDoesntSupportMessage(RestApiErrorCodes.REST_RecipientDoesntSupportMessage),
        
        /**
         * For SMS messages only. The recipient phone number cannot accept text messages
         */
    	RejectedByRecipient(RestApiErrorCodes.REST_RejectedByRecipient),
        
        /**
         * Sending messages to international numbers is not allowed for this extension/account.
         */
    	InternationalProhibited(RestApiErrorCodes.REST_InternationalProhibited),
        
        /**
         * The account balance is insufficient for sending a message. Status codes: 403 Forbidden.
         */
    	InsufficientFunds(RestApiErrorCodes.REST_InsufficientFunds),
        
        /**
         * The account does not support the feature.
         */
    	FeatureNotAvailable(RestApiErrorCodes.REST_FeatureNotAvailable),
        
        /**
         * One of the parameters is invalid, refer to parameterName field in response. Status codes: 400 Bad Request.
         */
    	InvalidParameter(RestApiErrorCodes.REST_InvalidParameter),
        
        /**
         * The content to be sent is invalid (empty, too long, etc.) Status codes: 400 Bad Request , 415 Unsupported Media Type.
         */
    	InvalidContent(RestApiErrorCodes.REST_InvalidContent),
    	
    	/**
         * Gateway rejected the SMS
         */
        GatewayRejected(RestApiErrorCodes.REST_GatewayRejected),

    	/**
         * Reply is forbidden for old message threads
         */
    	OldThreadReply(RestApiErrorCodes.REST_OldThreadReply),
    	
    	/**
         * Reply is denied for user, who is no longer a thread participant
         */
    	OutOfThreadReply(RestApiErrorCodes.REST_OutOfThreadReply);
    	
        private int mNumericErrorCode;
        private Codes(int code) {
            mNumericErrorCode = code;
        }

        public int getNumericErrorCode() {
            return mNumericErrorCode;
        }
        
    }
    
    public int numericErrorCode() {
        if (TextUtils.isEmpty(errorCode)) {
            return RestApiErrorCodes.REST_UNKNOWN_LOGICAL_ERROR;
        }

        for (Codes code : Codes.values()) {
            if (errorCode.startsWith(code.name())) {
                return code.getNumericErrorCode();
            }
        }
        return RestApiErrorCodes.REST_UNKNOWN_LOGICAL_ERROR;
    }



    /**
     * Human-readable text describing the error reason (can be <code>null</code>).
     */
    public String message;
    
    /**
     * High level logical error code (can be <code>null</code>).
     */
    public String errorCode;
    
    /**
     * For 400 Bad Request only. Name of incorrect parameter (can be <code>null</code>).
     */
    public String parameterName;
    
    /**
     * For 400 Bad Request only. Specified parameter value (can be <code>null</code>).
     */
    public String parameterValue;
    
    /**
     * For 400 Bad Request only. Description of an error (can be <code>null</code>).
     */
    public String description;
    
    /**
     * For 500 Internal server Error only. Internal identifier of an error (can be <code>null</code>).
     */
    public String eventId;
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("RestErrorResponse: \n")
        .append(" message:").append(getLogString(message)).append('\n')
        .append(" errorCode:").append(getLogString(errorCode)).append('\n')
        .append(" parameterName:").append(getLogString(parameterName)).append('\n')
        .append(" parameterValue:").append(getLogString(parameterValue)).append('\n')
        .append(" description:").append(getLogString(description)).append('\n')
        .append(" eventId:").append(getLogString(eventId)).append('\n');
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof RestErrorResponse)) {
            return false;
        }
        
        if (object == this) {
            return true;
        }
        
        RestErrorResponse r = (RestErrorResponse)object;
        
        if (TextUtils.equals(message, r.message)
                && TextUtils.equals(errorCode, r.errorCode)
                && TextUtils.equals(parameterName, r.parameterName)
                && TextUtils.equals(parameterValue, r.parameterValue)
                && TextUtils.equals(description, r.description)
                && TextUtils.equals(eventId, r.eventId)) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return 12797;
    }
    
    /**
     * Returns normalized string for logging.
     * 
     * @param string
     *            the string to be processed
     * @return normalized string for logging
     */
    private static final String getLogString(String string) {
        if (string == null) {
            return "NULL";
        } else {
            if (string.trim().length() == 0) {
                return "EMPTY";
            } else {
                return string;
            }
        }
    }
    
    /**
     * Called on starting HTTP response receiving (HTTP Status code is not 200).
     * 
     * @param response
     *            the HTTP response stream
     */
    public static RestErrorResponse onErrorResponse(Reader response) {
        RestErrorResponse ret = new RestErrorResponse();

        try {
            JsonReader jReader = new JsonReader(response);
            jReader.beginObject();
            while (jReader.hasNext()) {
                String name = jReader.nextName();
                if (name.equals("message")) {
                    ret.message = jReader.nextString();
                } else if (name.equals("errorCode")) {
                    ret.errorCode = jReader.nextString();
                } else if (name.equals("parameterName")) {
                    ret.parameterName = jReader.nextString();
                } else if (name.equals("parameterValue")) {
                    ret.parameterValue = jReader.nextString();
                } else if (name.equals("description")) {
                    ret.description = jReader.nextString();
                } else if (name.equals("eventId")) {
                    ret.eventId = jReader.nextString();
                } else {
                    jReader.skipValue();
                    MktLog.w("[RC]RestErrorResponse", "onErrorResponse unknown name : " + name);
                }
            }
            
            /**
             * Quick validation.
             */
            if (TextUtils.isEmpty(ret.errorCode)) {
                MktLog.i("[RC]RestErrorResponse", "onErrorResponse. errorCode is not defined.");
            } else {
                boolean found = false;
                for (Codes err : Codes.values()) {
                    if (err.name().equals(ret.errorCode)) {
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    MktLog.w("[RC]RestErrorResponse", "onErrorResponse. not known/processed errorCode : " + ret.errorCode);
                }
            }
            
            
            jReader.endObject();
            jReader.close();
        } catch (Throwable th) {
            MktLog.e("[RC]RestErrorResponse", "onErrorResponse processing : exception : " + th.toString(), th);
        }
        return ret;
    }
}
