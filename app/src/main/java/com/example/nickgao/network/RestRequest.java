/**
 * Copyright (C) 2012, RingCentral, Inc.
 *
 * All Rights Reserved.
 */
package com.example.nickgao.network;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.Header;
import org.apache.http.HttpMessage;

import com.example.nickgao.logging.MktLog;

/**
 * Base class for an HTTP REST requests.
 */
public abstract class RestRequest implements Comparable<RestRequest> {
    /**
     * Defines options for logging HTTP headers and bodies in requests/responses
     */
    public static enum LogHttp {
        NONE, HEADER_ONLY, ALL;
    }

    /**
     * Defines request method
     */
    public static enum HttpMethod {
        GET, POST, PUT, DELETE;
    }

    /**
     * Keeps internal state of the request.
     */
    private volatile InternalState mState = InternalState.INITIAL;

    /**
     * Keeps the result of request (one of value from {@link RESTStatusCode}) completion (Valid in
     * {@value InternalState#COMPLETED} state only)
     */
    private volatile int mResult = RestApiErrorCodes.UNKNOWN_STATUS_CODE;

    /**
     * Keeps unique sequence number.
     */
    private volatile long mSequenceNumber = RestSession.getSequenceNumber();

    /**
     * Constructs base request.
     *
     * @param method defines HTTP method for the request
     *
     * @param logTag
     *            a short logging tag be used for request operation logging as a prefix of message followed by id (see #
     *            getId()) in square brackets, example for "Account" logTag :
     *
     *            <code>
     * [RC]RESTRequest   Account[2345] Completed with status 200.
     * </code>
     *
     * @param logRequest defines logging options for request
     * @param logResponse defines logging options for response
     */
    public RestRequest(HttpMethod method, String logTag, LogHttp logRequest, LogHttp logResponse) {
        if (logTag == null) {
            new java.security.InvalidParameterException(LOG_TAG);
        }
        mLogTag = new String(logTag + "[id:" + mSequenceNumber + "]");
        mLogHttpRequest = logRequest;
        mLogHttpResponse = logResponse;
        mMethod = method;
    }

    /**
     * Keeps option for HTTP request logging.
     */
    LogHttp mLogHttpRequest = LogHttp.NONE;

    /**
     * Keeps option for HTTP response logging.
     */
    LogHttp mLogHttpResponse = LogHttp.NONE;

    /**
     * Keeps HTTP method.
     */
    HttpMethod mMethod;

    /**
     * Keeps the number of executions.
     */
    AtomicInteger totalExecutions = new AtomicInteger(0);

    /**
     * Keeps logging tag, see {@link #RESTRequest(String)}
     */
    protected String mLogTag;

    /**
     * Keeps mailboxId the request belongs, valid only after injection into RESTSession.
     */
    long mMailboxId;

    /**
     * Keeps factoryId the request belongs, valid only after injection into RESTSession.
     */
    long mFactoryId;

    /**
     * Defines if there was an authorization attempt for the request.
     */
    boolean wasReAuthAttempt;

    /**
     * Returns mailboxId the request assigned, the value
     * is valid only after {@link RestSession#sendRequest(RestRequest)} call.
     *
     * @return mailboxId of the request
     */
    public long getMailboxId() {
        return mMailboxId;
    }

    /**
     * Returns factoryId of the request.
     *
     * @return factoryId of the request
     */
    public long getFactoryId() {
        return mFactoryId;
    }

    /**
     * Returns an unique identifier across REST API implementation.
     *
     * @return an unique identifier of the request.
     */
    public long getId() {
        return mSequenceNumber;
    }

    /**
     * Returns result of the request execution as status code defined in {@link RESTStatusCode}. The status is valid
     * when the request is not active (see {@link #isActive()})
     *
     * @return result of the request
     */
    public int getResult() {
        return mResult;
    }

    /**
     * Returns HTTP status code if connection has been established and response received
     *
     * @return HTTP status code
     */
    public int getHttpCode() {
        return mHttpCode;
    }

    /**
     * Keeps HTTP returned status code;
     */
    int mHttpCode = 500;

    /**
     * Copies status data from indicated request.
     *
     * @param request the request to take status data
     */
    public void setStatusFrom(RestRequest request) {
        mHttpCode = request.mHttpCode;
        mResult = request.mResult;
        mRestErrorResponse = request.mRestErrorResponse;
    }

    /**
     * Turn the request to completion state.
     *
     * @param result the result of completion, see {@link RESTStatusCode}
     *
     * @param callOnCompletion tells if {@link #onCompletion()} invocation required
     */
    void setCompletedState(int result, boolean callOnCompletion) {
        if (mState == InternalState.COMPLETED) {
            MktLog.e(LOG_TAG, mLogTag + " setCompletedState : already completed");
            return;
        }

        setResult(result);

        turnedToState(InternalState.COMPLETED);

        if (callOnCompletion) {
            try {
                onCompletion();
            } catch (Throwable th) {
                MktLog.e(LOG_TAG, mLogTag + " onCompletion : exception : " + th.toString(), th);
            }
        }
    }

    /**
     * Keeps REST Error Response if processed.
     */
    protected RestErrorResponse mRestErrorResponse;

    /**
     * Returns REST Error Response if HTTP Code was not 200 (OK) and the error response was processed.
     *
     * @return REST Error Response if HTTP Code was not 200 (OK) and the error response was processed (can be
     *         <code>null</code>)
     */
    public RestErrorResponse getErrorResponse() {
        return mRestErrorResponse;
    }

    /**
     * Sets the result of completion.
     *
     * @param result the result of completion, see {@link RESTStatusCode}
     */
    protected void setResult(int result) {
        if (mResult != result) {
            mResult = result;
            MktLog.d(LOG_TAG, mLogTag + " setResult " + RestApiErrorCodes.getMsg(result));
        }
    }

    /**
     * Turns the request to new state.
     *
     * @param newState new state
     */
    void turnedToState(InternalState newState) {
        InternalState prevState = mState;
        if (newState != prevState) {
            mState = newState;
            MktLog.d(LOG_TAG, mLogTag + " State change " + prevState.name() + " -> " + newState.name()) ;
        }
    }

    InternalState getState() {
        return mState;
    }

    @Override
    public int compareTo(RestRequest other) {
        if (mSequenceNumber == other.mSequenceNumber) {
            return 0;
        }
        return (mSequenceNumber < other.mSequenceNumber ? -1 : 1);
    }

    /**
     * Tells whether the request completed or not.
     *
     * @return <code>true</code> if the request is still in processing, otherwise <code>false</code>
     */
    public boolean isActive() {
        return mState != InternalState.COMPLETED;
    }

    /**
     * Constants enumerating the status of request.
     */
    enum InternalState {
        INITIAL, PENDING_EXECUTION, PENDING_AUTHORIZATION, EXECUTION, COMPLETED
    }

    /**
     * Called on request completion. The result of execution can be obtained via {@link #getResult()}
     */
    public abstract void onCompletion();

    /**
     * Called on starting HTTP response receiving (HTTP Status code is 200).
     *
     * @param response
     *            the HTTP response stream
     * @param contentType
     *            the Content-Type header, or null if the content type is unknown
     * @param contentEncoding
     *            the Content-Encoding header for this response, or null if the content encoding is unknown
     * @param length
     *            the number of bytes of the content, or a negative number if unknown. If the content length is known
     *            but exceeds Long.MAX_VALUE, a negative number is returned.
     * @param headers
     *            all the headers of this response. Headers are ordered in the sequence they will be sent over a connection.
     */
    public abstract void onResponse(Reader response, InputStream responseStream,
                                    String contentType, String contentEncoding, long length, Header[] headers)
            throws java.io.IOException;

    /**
     * Returns the path of the request. The method will be called if {@link #getURI()} returns <code>null</code>.
     *
     * @return the path of request.
     */
    public abstract String getPath();

    /**
     * Returns URI of the request (scheme/host/path). The method will be called before {@link #getPath()}, if the method
     * (sub-class) will return <code>null</code>, {@link #getPath()} will be called.
     *
     * @return URI of the request or <code>null</code> so the implementation will call {@link #getPath()}
     */
    public String getURI() {
        return null;
    }

    /**
     * Returns the body content to be sent in the request. Sub-classes shall override the method if required
     *
     * @return the content (body) to be sent
     */
    public String getBody() {
        return "";
    }
    
    
    /**
     * 
     * @return
     */
    public boolean hasAttachment() {
    	return false;
    }
    
    /**
     * 
     * @return
     */
    public ByteArrayOutputStream getAttachment() {
    	return null;
    }

    /**
     * Called on HTTP header forming. Sub-classes shall override the method if required
     *
     * @param  request the request for headers adding/modification
     */
    public void onHeaderForming(HttpMessage request) {
    }

    /**
     * Defines logging tag.
     */
    private static final String LOG_TAG = "[RC]RESTRequest";

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer().append(mLogTag).append("; id:").append(this.mSequenceNumber)
        .append("; factoryId:").append(mFactoryId)
        .append("; mailboxId:").append(mMailboxId)
        .append("; state:").append(mState.name())
        .append("; result:").append(RestApiErrorCodes.getMsg(mResult));
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof RestRequest)) {
            return false;
        }
        
        RestRequest r = (RestRequest)object;
        
        if (r.mSequenceNumber == mSequenceNumber) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return (int)mSequenceNumber;
    }
}
