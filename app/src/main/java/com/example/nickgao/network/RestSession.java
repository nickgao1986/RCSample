/** 
 * Copyright (C) 2012-2013, RingCentral, Inc.
 *  
 * All Rights Reserved.
 */
package com.example.nickgao.network;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;

import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.GeneralSettings;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.database.RCMProviderHelper;
import com.example.nickgao.logging.BUILD;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.utils.HttpUtils;
import com.example.nickgao.utils.HttpUtils.HttpResponseLogger;
import com.example.nickgao.utils.NetworkUtils;
import com.example.nickgao.utils.Utils;
import com.example.nickgao.utils.execution.CommandProcessor;
import com.example.nickgao.utils.execution.CommandProcessor.Command;
import com.google.agson.stream.JsonReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Management interface for REST API. 
 */
public final class RestSession {
    /**
     * Defines max.length of response body to be logged (if requested)
     */
    private static final int MAX_BODY_LENGTH_FOR_LOGGING = 10 * 1024;
    
    /**
     * Defines the timeout in milliseconds (provided by platform team) for calculation of next forced authorization
     * refresh by excluding from elapsed time. Value is 60 seconds.
     */
    private static final long TCP_TIMEOUT_EXP_SUB = 2 * 30 * 1000;

    /**
     * Defines the minimal timeout in milliseconds between authorizations on sending requests if access token defined as
     * expired. Value is 60 seconds.
     */
    private static final long MIN_TIMEOUT_BETWEEN_AUTH_REQS = 1 * 60 * 1000;
    
    private static final long REQUEST_TRY_INTERNAL = 100;
    private static final int MAX_TRIES = 2;//0~2 3times
    private static final long BASIC_TOKEN_EXPIRATION_INTERVAL = 60 * 60 * 24 * 7; // 7 days
    
    private static final String TAG = "[RC]RestSession";
    
    /**
     * Hidden constructor.
     * 
     * @param mailboxId
     *            the mailboxId of session
     */
    private RestSession(long mailboxId) {
        mFactoryId = sFactoryId.get();
        mMailboxId = mailboxId;
    }
    
    /**
     * Returns mailboxId of the session.
     * 
     * @return mailboxId of the session
     */
    public long getMailBoxId() {
        return mMailboxId;
    }
    
    /**
     * Returns factoryId of the session.
     * 
     * @return factoryId of the session
     */
    public long getFactoryId() {
        return mFactoryId;
    }

    /**
     * Returns status code for {@link RestSessionState#AUTHORIZATION_FAILED} state:
     * <ul>
     * <li>
     * {@link RestApiErrorCodes#RESPONSE_PROCESSING_ERROR}</li>
     * <li>
     * {@link RestApiErrorCodes#RESPONSE_INVALID_FORMAT}</li>
     * <li>
     * {@link RestApiErrorCodes#RESPONSE_HTTP_STATUS_ERROR}</li>
     * <li>
     * {@link RestApiErrorCodes#CONNECTION_ERROR}</li>
     * <li>
     * {@link RestApiErrorCodes#AUTHORIZATION_ERROR}, critical error, the account does not exist, disabled, in invalid
     * state or password has been changed</li>
     * </ul>
     * 
     * @return status code for {@link RestSessionState#AUTHORIZATION_FAILED} state
     */
    public int getStatusCode() {
        return mStatusCode;
    }

    /**
     * A request to destroy the session.
     * 
     * @param notify
     *            defines if notification required
     * @param cleanPersistentData
     *            defines if persistent data shall be cleaned
     */
    public void destroy(boolean notify, boolean cleanPersistentData) {
        destroyInner(true, notify, cleanPersistentData);
    }
    
    /**
     * A request to destroy the session.
     * 
     * @param notify
     *            defines if notification required
     * @param cleanPersistentData
     *            defines if persistent data shall be cleaned
     */
    private void destroyInner(boolean lock, boolean notify, boolean cleanPersistentData) {
        MktLog.w(LOG_TAG, mMailboxId + ".destroy called.");
        if (lock) {
            LOCK();
        }
        List<RestRequest> requests = null;
        try {
            if (mMailboxId != 0) {
                Context ctx = RingCentralApp.getContextRC();
                if (ctx != null) {
                    RCMProviderHelper.setRestRefreshToken(ctx, mMailboxId, null);
                }
            }
            if (mState == RestSessionState.DESTROYED) {
                MktLog.w(LOG_TAG, mMailboxId + ".destroy: already destroyed.");
            } else {
                sSessions.remove(mMailboxId);
                try {
                    RestHttpClient client = mHttpClient;
                    if (mHttpClient != null) {
                        mHttpClient = null;
                        client.shutdown();
                    }
                } catch (Throwable th) {
                    MktLog.e(LOG_TAG, mMailboxId + ".destroy: HTTP client shutdown failed : exception " + th.toString());
                }
                onStateChange(RestSessionState.DESTROYED, RestApiErrorCodes.NO_ERROR);
                requests = pollPendentRequests(mFactoryId, mMailboxId);
            }
            if (lock) {
                UNLOCK(mMailboxId + ".destroy");
            }
            
            if (requests != null) {
                for (RestRequest rest : pollPendentRequests(mFactoryId, mMailboxId)) {
                    sRequests.getAndDecrement();
                    requestToCompletion(rest, RestApiErrorCodes.INVALID_SESSION_STATE, true);
                }
            }
            
            if (notify) {
                sendStateChangeNotification(mFactoryId, mMailboxId, RestSessionState.DESTROYED, RestApiErrorCodes.NO_ERROR);
            }
        } finally {
            if (lock) {
                UNLOCK_CHECK_UP(mMailboxId + ".destroy");
            }
        }
    }    
    
    /**
     * Returns current state of the session.
     * 
     * @return current state of the session
     */
    public RestSessionState getState() {
        return mState;
    }

    /**
     * Returns existing RESTSession.
     * 
     * @param mailboxId
     *            the mailboxId of session
     * 
     * @return <code>null</code> if session has not been found (not created or was destroyed), otherwise the session
     *         instance
     */
    public static RestSession get(long mailboxId) {
        LOCK();
        try {
           return sSessions.get(mailboxId);
        } finally {
            UNLOCK("RestSession.get");
        }
    }
    
    /**
     * Destroys current sessions factory.
     * 
     * @param notify
     *            defines if notifications required
     * @param cleanPersistentData
     *            defines if persistent data shall be cleaned
     */
    public static void destroyAll(boolean notify, boolean cleanPersistentData) {
        MktLog.w(LOG_TAG, "destroyAll (factoryId:" + sFactoryId.get() + ")");
        sFactoryId.getAndIncrement();
        LOCK();
        try {
            ArrayList<RestSession> sessions = new ArrayList<RestSession>();
            for (RestSession session : sSessions.values()) {
                sessions.add(session);
            }
            for (RestSession session : sessions) {
                try {
                    session.destroyInner(false, notify, cleanPersistentData);
                } catch (Throwable th) {
                    MktLog.e(LOG_TAG, "destroyAll : exception : " + th.toString());
                }
            }
            sSessions.clear();
            sPendingQueue.clear();
            UNLOCK("destroyAll");
            MktLog.d(LOG_TAG, "destroyAll : " + dumpState());
        } catch (Throwable th) {
        } finally {
            UNLOCK_CHECK_UP("destroyAll");    
        }
    }
    
    /**
     * Creates a session if not exists, otherwise returns existent.
     * 
     * @param mailboxId
     *            the mailboxId of session
     * 
     * @return new session in initial state or existent
     */
    public static RestSession createSession(long mailboxId) {
        MktLog.i(LOG_TAG, mailboxId + ".createSession");

        sCreateSessionCalls.getAndIncrement();
        
        if (mailboxId == 0) {
            MktLog.w(LOG_TAG, mailboxId + ".createSession: invalid mailboxId.");
            return null;
        }

        RestSession session = null;

        LOCK();
        try {
            session = sSessions.get(mailboxId);
            if (session == null) {
                session = new RestSession(mailboxId);
                sSessions.put(mailboxId, session);
            }
        } finally {
            UNLOCK("RestSession.createSession");
        }

        return session;
    }
    
    /**
     * Request to make an authorization
     * 
     * @param userName
     *            the user name
     * @param extension
     *            the optional extension
     * @param password
     *            the login password
     * @return <code>true</code> in case of staring processing of the request,otherwise <code>false</code>
     */
    public boolean authorize(String userName, String extension, String password) {
        MktLog.i(LOG_TAG, mMailboxId + ".authorize");
        
        sAuthorizeCalls.getAndIncrement();
        
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            MktLog.e(LOG_TAG, mMailboxId + ".authorize: invalid credentials.");
            return false;
        }

        int netStatus = getNetworkState();
        
        LOCK();
        try {
            if (sFactoryId.get() != mFactoryId) {
                mState = RestSessionState.DESTROYED;
                UNLOCK("authorize:factoryId");
                MktLog.e(LOG_TAG, mMailboxId + ".authorize: invalid session state (factory id).");
                return false;
            }

            if (mState == RestSessionState.DESTROYED) {
                UNLOCK("authorize:destroyed");
                MktLog.w(LOG_TAG, mMailboxId + ".authorize: invalid session state");
                return false;
            }

            if (netStatus != RestApiErrorCodes.OK) {
                if (mState != RestSessionState.AUTHORIZATION_FAILED) {
                    onStateChange(RestSessionState.AUTHORIZATION_FAILED, netStatus);
                    UNLOCK("authorize:net:failed1");
                    sendStateChangeNotification(mFactoryId, mMailboxId, RestSessionState.AUTHORIZATION_FAILED, netStatus);
                } else {
                    UNLOCK("authorize:net:failed2");
                }
                MktLog.w(LOG_TAG, mMailboxId + ".authorize: invalid network state");
                return false;
            }

            Context ctx = RingCentralApp.getContextRC();
            
            if (ctx == null) {
                UNLOCK("authorize:context");
                MktLog.e(LOG_TAG, mMailboxId + ".authorize: context is null");
                return false;
            }
            
            if (mHttpClient == null) {
                mHttpClient = RestHttpClient.getClient(mMailboxId);
                if (mHttpClient == null) {
                    UNLOCK("authorize:http_client");
                    MktLog.w(LOG_TAG, mMailboxId + ".authorize: invalid HTTP client.");
                    return false;
                }
            }
            
            AuthContext authContext = new AuthContext();
            authContext.authString = getAuthorizationBody(userName, extension, password, false);
            authContext.authLogString = getAuthorizationBody(userName, extension, password, true);
            authContext.refreshToken = null;
            authContext.host = getHost(ctx);

            boolean cleanRefreshToken = true;
            switch (mState) {
            case INITIAL:
                MktLog.i(LOG_TAG, mMailboxId + ".authorize: from INITIAL state.");
                cleanRefreshToken = false;
                break;
            case AUTHORIZATION:
                if (authContext.authString.equals(mAuthContext.authString)) {
                    UNLOCK("authorize:state_authorization");
                    MktLog.w(LOG_TAG, mMailboxId + ".authorize(AUTHORIZATION): waiting AUTHORIZATION");
                    sendStateChangeNotification(mFactoryId, mMailboxId, RestSessionState.AUTHORIZATION, RestApiErrorCodes.NO_ERROR);
                    return true;
                } else {
                    MktLog.w(LOG_TAG, mMailboxId + ".authorize(AUTHORIZATION): new AUTHORIZATION (other credentials)");
                }
                break;
            case AUTHORIZED:
                if (authContext.authString.equals(mAuthContext.authString)) {
                    MktLog.i(LOG_TAG, mMailboxId + ".authorize(AUTHORIZED): ommited, just notification sent");
                    UNLOCK("authorize:state_authorized");
                    sendStateChangeNotification(mFactoryId, mMailboxId, RestSessionState.AUTHORIZED, RestApiErrorCodes.NO_ERROR);
                    return true;
                } else {
                    MktLog.w(LOG_TAG, mMailboxId + ".authorize(AUTHORIZED): new AUTHORIZATION ((other credentials))");
                }
                break;
            case AUTHORIZATION_FAILED:
                if (authContext.authString.equals(mAuthContext.authString)) {
                    if (mStatusCode == RestApiErrorCodes.AUTHORIZATION_ERROR) {
                        // Allow re-authorize as the user can return, for example, the password back.
                        MktLog.e(LOG_TAG, mMailboxId + ".authorize(AUTHORIZATION_FAILED): status AUTHORIZATION_ERROR for the same credentials.");
                    } else {
                        MktLog.w(LOG_TAG, mMailboxId + ".authorize(AUTHORIZATION_FAILED): not critical state : the same credentials.");    
                    }
                } else {
                    MktLog.w(LOG_TAG, mMailboxId + ".authorize(AUTHORIZATION_FAILED): new AUTHORIZATION");
                }
                break;
            default:
                MktLog.w(LOG_TAG, mMailboxId + ".authorize(DESTROYED): unreachable state.");
                UNLOCK("authorize:unknown_state");
                return false;
            }
            mAuthContext = authContext;
            mAuthContext.authId = getSequenceNumber();
            
            if (cleanRefreshToken) {
                if (!TextUtils.isEmpty(mAuthContext.refreshToken)) {
                    RCMProviderHelper.setRestRefreshToken(ctx, mMailboxId, null);
                }
                mAuthContext.refreshToken = null;
            }
            
            AuthRequestContext auxCtx = getRequestContext();
            onStateChange(RestSessionState.AUTHORIZATION, RestApiErrorCodes.NO_ERROR);
            UNLOCK("authorize-final");
            sendStateChangeNotification(mFactoryId, mMailboxId, RestSessionState.AUTHORIZATION, RestApiErrorCodes.NO_ERROR);
            postAuthorizationCommand(auxCtx);
        } finally {
            UNLOCK_CHECK_UP("authorize");
        }
        return true;
    }
    
    /**
     * Create an authorization request based on the session and an authorization context.
     * 
     * @param authContext
     *            the authorization context
     * 
     * @return authorization request
     */
    private AuthRequestContext getRequestContext() {
        AuthRequestContext authReqCtx = new AuthRequestContext();
        authReqCtx.authId        = mAuthContext.authId;
        authReqCtx.mailboxId     = mMailboxId;
        authReqCtx.httpClient    = mHttpClient;
        authReqCtx.mFactoryId    = mFactoryId;
        authReqCtx.authString    = mAuthContext.authString;
        authReqCtx.authLogString = mAuthContext.authLogString;
        authReqCtx.host          = mAuthContext.host;
        authReqCtx.lastRefreshToken = mAuthContext.refreshToken;
        return authReqCtx;
    }

    public static boolean validateSession(Context context) {
        long mailboxId = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
        if (RCMDataStore.MailboxCurrentTable.MAILBOX_ID_NONE == mailboxId) {
            return false;
        }

        RestSession session = RestSession.get(mailboxId);
        if (session == null) {
            return false;
        }

        if (session.mAuthContext == null){
            return false;
        }

        if (session.getState() == RestSessionState.INITIAL) {
            return false;
        }
        if (session.getState() == RestSessionState.AUTHORIZATION_FAILED &&
                session.getStatusCode() == RestApiErrorCodes.AUTHORIZATION_ERROR) {
            MktLog.w(LOG_TAG, "validateSession(): session is in un-recoverable " +
                    "authorization failed state (account) for mailboxId:" + mailboxId + "; stop checking account state.");
            return false;
        }
        return true;
    }


    /**
     * Sends a request
     * 
     * @param request
     *            the request to send
     * 
     * @return <code>true</code> in case of staring processing of the request,otherwise <code>false</code>
     */
    public boolean sendRequest(final RestRequest request) {
        // TODO: Check-consistency command
        // TODO: Add time-stamp for requests
        // TODO: Add cyclic-check for auths
        
        sSendRequestsCalls.getAndIncrement();
        
        if (request == null) {
            sSyncFailedRequests.getAndIncrement();
            MktLog.e(LOG_TAG, mMailboxId + ".sendRequest : invalid parameters.");
            return false;
        }

        MktLog.d(LOG_TAG, mMailboxId + ".sendRequest : " + request.mLogTag);
        
        int netStatus = getNetworkState();

        LOCK();
        try {
            if (request.getState() != RestRequest.InternalState.INITIAL) {
                UNLOCK("sendRequest1");
                sSyncFailedRequests.getAndIncrement();
                MktLog.e(LOG_TAG, mMailboxId + ".sendRequest : request is not in INITIAL state.");
                return false;
            }

            if (netStatus != RestApiErrorCodes.OK) {
                UNLOCK("sendRequest2");
                sSyncFailedRequests.getAndIncrement();
                MktLog.e(LOG_TAG, mMailboxId + ".sendRequest : invalid network state");
                request.setCompletedState(netStatus, false);
                return false;
            }

            if (mState == RestSessionState.DESTROYED || mState == RestSessionState.INITIAL) {
                UNLOCK("sendRequest3");
                sSyncFailedRequests.getAndIncrement();
                MktLog.e(LOG_TAG, mMailboxId + ".sendRequest : not valid session state for sending requests.");
                request.setCompletedState(RestApiErrorCodes.INVALID_SESSION_STATE, false);
                return false;
            }

            request.mFactoryId = sFactoryId.get();
            request.mMailboxId = mMailboxId;
            
            if (mState == RestSessionState.AUTHORIZATION) {
                request.wasReAuthAttempt = false;
                sPendingQueue.add(request);
                sRequests.getAndIncrement();
                UNLOCK("sendRequest4");
                request.turnedToState(RestRequest.InternalState.PENDING_AUTHORIZATION);
                MktLog.i(LOG_TAG, request.mLogTag + ".sendRequest (session is in AUTHORIZATION state): pending request ");
                return true;
            }
            
            if (mState == RestSessionState.AUTHORIZATION_FAILED) {
                if (mStatusCode == RestApiErrorCodes.AUTHORIZATION_ERROR) {
                    MktLog.w(LOG_TAG, mMailboxId + ".sendRequest : un-authorized : not valid state of the account.");
                    UNLOCK("sendRequestAuthError");
                    sSyncFailedRequests.getAndIncrement();
                    request.setCompletedState(RestApiErrorCodes.AUTHORIZATION_ERROR, false);
                    return false;
                }
                
                request.wasReAuthAttempt = false;
                sPendingQueue.add(request);
                sRequests.getAndIncrement();
                mAuthContext.authId = getSequenceNumber();
                AuthRequestContext auxCtx = getRequestContext();
                onStateChange(RestSessionState.AUTHORIZATION, RestApiErrorCodes.NO_ERROR);
                UNLOCK("sendRequest5");
                sendStateChangeNotification(mFactoryId, mMailboxId, RestSessionState.AUTHORIZATION, RestApiErrorCodes.NO_ERROR);
                request.turnedToState(RestRequest.InternalState.PENDING_AUTHORIZATION);
                MktLog.i(LOG_TAG, request.mLogTag + ".sendRequest (session is in AUTHORIZATION_FAILED state): re-authorize attempt");
                postAuthorizationCommand(auxCtx);
                return true;
            }
            
            
            /** 
             * In this case mState = AUTHORIZED
             * Checks if accessToken Expired 
            */
            long accessTokenExpirationTime = mAuthContext.lastAuthCmplElpsdTime
                    + (mAuthContext.lastAuthExpiresIn * 1000) - TCP_TIMEOUT_EXP_SUB;
            long currentTime = SystemClock.elapsedRealtime();
            long expiration = accessTokenExpirationTime - currentTime;
            if (expiration < 0) {
                boolean makeAuth = true;
                if (mAuthContext.lastAuthCmplElpsdTime > 0) {
                    long delta = currentTime - mAuthContext.lastAuthCmplElpsdTime;
                    if (delta < MIN_TIMEOUT_BETWEEN_AUTH_REQS) {
                        MktLog.w(LOG_TAG,
                                ".sendRequest: token expiration defined, ommited as last auth. was " + delta
                                        + " ms ago when threshold is " + MIN_TIMEOUT_BETWEEN_AUTH_REQS);
                        makeAuth = false;
                    }
                }
                
                if (makeAuth) {
                    request.wasReAuthAttempt = false;
                    sPendingQueue.add(request);
                    sRequests.getAndIncrement();
                    mAuthContext.authId = getSequenceNumber();
                    AuthRequestContext auxCtx = getRequestContext();
                    onStateChange(RestSessionState.AUTHORIZATION, RestApiErrorCodes.NO_ERROR);
                    UNLOCK("sendRequest:auth:expired");
                    
                    StringBuffer sb = new StringBuffer("");
                    sb.append(request.mLogTag).append(".sendRequest (session is in AUTHORIZED state): forced re-authorization due to token expiration :");
                    sb.append(" l:").append(mAuthContext.lastAuthCmplElpsdTime);
                    sb.append(" c:").append(currentTime);
                    sb.append(" e:").append(mAuthContext.lastAuthExpiresIn * 1000);
                    sb.append(" d:").append(expiration);
                    sb.append(" t:").append(currentTime - mAuthContext.lastAuthCmplElpsdTime);
                    MktLog.i(LOG_TAG, sb.toString()); 
                    sendStateChangeNotification(mFactoryId, mMailboxId, RestSessionState.AUTHORIZATION, RestApiErrorCodes.NO_ERROR);
                    request.turnedToState(RestRequest.InternalState.PENDING_AUTHORIZATION);
                    postAuthorizationCommand(auxCtx);
                    return true;
                }
            }
            
            UNLOCK("sendRequest-final");
        } finally {
            UNLOCK_CHECK_UP("sendRequest");
        }
        request.turnedToState(RestRequest.InternalState.PENDING_EXECUTION);
        sRequests.getAndIncrement();
        postRequestCommand(getRequestContext(request));
        return true;
    }
    
    /**
     * Creates context for a request
     * 
     * @param request
     *            the request to create execution context
     * @return the execution context
     */
    private RequestContext getRequestContext(RestRequest request) {
        RequestContext requestCtx = new RequestContext();
        requestCtx.authId      = mAuthContext.authId;
        requestCtx.accessToken = mAuthContext.accessToken;
        requestCtx.host        = mAuthContext.host;
        requestCtx.mailboxId   = mMailboxId;
        requestCtx.httpClient  = mHttpClient;
        requestCtx.request     = request;
        requestCtx.mFactoryId  = mFactoryId;
        return requestCtx;
    }
    
    
    /**
     * Posts request command.
     * 
     * @param requestCtx the request context to be executed
     */
    private static void postRequestCommand(final RequestContext requestCtx) {
        try {
            MktLog.d(LOG_TAG, ".postRequestCommand : " + requestCtx.request.mLogTag);
            sExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    sThreads.incrementAndGet();
                    try {
                        requestCommand(requestCtx);
                    } finally {
                        sThreads.decrementAndGet();
                    }
                }
            });
        } catch (Throwable th) {
            MktLog.e(LOG_TAG, "postRequestCommand : exception : " + th.toString());
        }
    }
    
    /**
     * Request command (executed on certain thread).
     * 
     * @param authReqCtx the authorization context to be executed
     */
    private static void requestCommand(final RequestContext requestCtx) {
        if (requestCtx == null) {
            MktLog.e(LOG_TAG, "requestCommand : request is null.");
            return;
        }

        if (requestCtx.mFactoryId != sFactoryId.get()) {
            MktLog.w(LOG_TAG, "requestCommand : omitted, invalid factory.");
            sRequests.getAndDecrement();
            requestToCompletion(requestCtx.request, RestApiErrorCodes.INVALID_SESSION_STATE, true);
            return;
        }

        try {
        	int maxTries = 0;
        	boolean loop = true;
        	while(loop){
        		//Check network
        		int netStatus = getNetworkState();
        		if(netStatus != RestApiErrorCodes.OK){
        			MktLog.d(LOG_TAG, "requestCommand: invalid network state,break loop.");
        			break;
        		}
        		//SYNC,execute command.
        		makeRequest(requestCtx);
        		
        		final RestRequest request = requestCtx.request;
        		int httpCode = request.mHttpCode;
        		
                MktLog.d(LOG_TAG, "requestCommand :loop current http code ="+httpCode
                		+ " current tries="+maxTries);
                //handle error.
                switch(httpCode){
                case RestApiErrorCodes.SC_OK_200:
                case RestApiErrorCodes.SC_CREATED_201:
                case RestApiErrorCodes.SC_NO_CONTENT_204:
                case RestApiErrorCodes.SC_MULTI_STATUS_207:{
                	//still,we need to check reponse is ok
                	if(requestCtx.request.getResult() == RestApiErrorCodes.OK){
                		loop = false;
                    	MktLog.d(LOG_TAG, "requestCommand : http status code=ok,loop=false");
                	}else{
                		MktLog.d(LOG_TAG, "requestCommand : http status code=ok,but result code=false,need to loop");
                		//need to repeat request
                		maxTries ++;
                		if(maxTries > MAX_TRIES){
                			loop = false;
                    		break;
                    	}
                		
                		try{
                    		//sleep REQUEST_TRY_INTERNAL
                    		Thread.sleep(REQUEST_TRY_INTERNAL);
                    	}catch(Throwable th){
                    		MktLog.e(LOG_TAG, "requestCommand : request.");
                    	}
                	}
                }
                break;
                case RestApiErrorCodes.SC_MULTIPLE_REQUEST_429:
                case RestApiErrorCodes.SC_REQUEST_TIMEOUT_408:
                case RestApiErrorCodes.SC_INTERNAL_SERVER_ERROR_500:
                case RestApiErrorCodes.SC_BAD_GATEWAY_502:
                case RestApiErrorCodes.SC_SERVICE_UNAVAILABLE_503:
                case RestApiErrorCodes.SC_GATEWAY_TIMEOUT_504:{
                	maxTries ++;
            		if(maxTries > MAX_TRIES){
            			loop = false;
                		break;
                	}
                	try{
                		//sleep REQUEST_TRY_INTERNAL
                		Thread.sleep(REQUEST_TRY_INTERNAL);
                	}catch(Throwable th){
                		MktLog.e(LOG_TAG, "requestCommand : request.");
                	}
                }
                break;
                case RestApiErrorCodes.CLIENT_INVALID_ERROR:{
                	//invalidate parameter for request,just break loop.
                	MktLog.d(LOG_TAG, "requestCommand : invalidate parameter for request,just break loop");
                	loop = false;
                }
                break;
                case RestApiErrorCodes.SC_BAD_REQUEST_400:
                case RestApiErrorCodes.SC_UNAUTHORIZED_401:
                case RestApiErrorCodes.SC_FORBIDDEN_403:
                case RestApiErrorCodes.SC_NOT_FOUND_404:
                default:{
                	MktLog.d(LOG_TAG, "requestCommand : DO NOT repeat the request. Display error message");
                	loop = false;
                }
                break;
                }
            }
        	
            //makeRequest(requestCtx);
            sProcessor.execute(new Command("OnRequestCompletion") {
                @Override
                public void run() {
                    onRequest(requestCtx);
                }
            });
        } catch (Throwable th) {
            MktLog.e(LOG_TAG, "requestCommand : exception : " + th.toString());
            
            try {
                requestToCompletion(requestCtx.request, RestApiErrorCodes.INVALID_REQUEST, true);
            } catch (Throwable thIn) {
            }
        }
    }
    
    /**
     * Posts authorization command.
     * 
     * @param authReqCtx
     *            the authorization context to be executed
     */
    private static void postAuthorizationCommand(final AuthRequestContext authReqCtx) {
        MktLog.d(LOG_TAG, "postAuthorizationCommand called.");
        try {
            sExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    sThreads.incrementAndGet();
                    try {
                        authorizationCommand(authReqCtx);
                    } finally {
                        sThreads.decrementAndGet();
                    }
                }
            });
        } catch (Throwable th) {
            onAuthError("postAuthorizationCommand : exception : " + th.toString(), authReqCtx);
        }
    }    
    
    /**
     * Authorization command.
     * 
     * @param authReqCtx the authorization context to be executed
     */
    private static void authorizationCommand(final AuthRequestContext authReqCtx) {
        MktLog.d(LOG_TAG, "authorizationCommand called.");
        if (authReqCtx == null) {
            MktLog.e(LOG_TAG, "authorizationCommand : auth. request is null.");
            return;
        }

        if (authReqCtx.mFactoryId != sFactoryId.get()) {
            MktLog.w(LOG_TAG, "authorizationCommand : omitted, invalid factory.");
            return;
        }
        
        try {
        	final Context ctx = RingCentralApp.getContextRC();
        	int maxTries = 0;
        	boolean refreshToken = !TextUtils.isEmpty(authReqCtx.lastRefreshToken);
        	boolean isRefreshToken = refreshToken;
        	boolean loop = true;
        	while(loop){
        		//Check network
        		int netStatus = getNetworkState();
        		if(netStatus != RestApiErrorCodes.OK){
        			MktLog.d(LOG_TAG, "authorizationCommand: invalid network state,break loop.");
        			break;
        		}
        		//SYNC,execute command.
                makeAuthorization(authReqCtx, isRefreshToken);
                MktLog.d(LOG_TAG, "authorizationCommand :loop current error code ="+authReqCtx.httpStatusCode
                		+ " current tries="+maxTries);
                //handle error.
                switch(authReqCtx.httpStatusCode){
                case RestApiErrorCodes.SC_OK_200:
                case RestApiErrorCodes.SC_CREATED_201:
                case RestApiErrorCodes.SC_NO_CONTENT_204:
                case RestApiErrorCodes.SC_MULTI_STATUS_207:{
                	//still,we need to check reponse is ok
                	if(authReqCtx.result == RestApiErrorCodes.OK){
                		loop = false;
                    	MktLog.d(LOG_TAG, "authorizationCommand : http status code=ok,loop=false");
                	}else{
                		MktLog.d(LOG_TAG, "authorizationCommand : http status code=ok,but result code=false,need to loop");
                		//need to repeat request
                		maxTries ++;
                		if(maxTries > MAX_TRIES){
                			if(isRefreshToken){
                    			//Do not repeat it. we need to perform OAuth authentication to get new tokens.
                    			refreshToken = isRefreshToken = false;
                    			maxTries = 0;
                    			//clear refresh token.
                    			authReqCtx.lastRefreshToken = null;
                    			RCMProviderHelper.setRestRefreshToken(ctx, authReqCtx.mailboxId, null);
                    		}else{
                    			//perform OAuth authentication,max tries.
                    			loop = false;
                    			break;
                    		}
                    	}
                		
                		try{
                    		//sleep REQUEST_TRY_INTERNAL
                    		Thread.sleep(REQUEST_TRY_INTERNAL);
                    	}catch(Throwable th){
                    		MktLog.e(LOG_TAG, "authorizationCommand : request.");
                    	}
                    	//reset request
                    	authReqCtx.result = RestApiErrorCodes.NO_ERROR;
                        authReqCtx.httpStatusCode = HttpStatus.SC_OK;
                	}
                }
                break;
                case RestApiErrorCodes.SC_BAD_REQUEST_400:{
                	MktLog.d(LOG_TAG, "authorizationCommand : Bad Request,invalid_grant,do not repeat request.");
                	if(isRefreshToken && authReqCtx.error != null && RestApiErrorCodes.ERROR_CODE_INVALID_GRANT.equals(authReqCtx.error)){
                		//refresh token is expired,Perform OAuth authentication to get new tokens
                    	MktLog.d(LOG_TAG, "authorizationCommand : refresh token is expired,Perform OAuth authentication to get new tokens.");
                   	 	authReqCtx.result = RestApiErrorCodes.NO_ERROR;
                        authReqCtx.httpStatusCode = HttpStatus.SC_OK;
                        authReqCtx.lastRefreshToken = null;
                        refreshToken = isRefreshToken = false;
                        maxTries = 0;
                	}else{
                		loop = false;
                		MktLog.d(LOG_TAG, "authorizationCommand : loop = false");
                	}
                }
                break;
                case RestApiErrorCodes.SC_MULTIPLE_REQUEST_429:{
                	maxTries ++;
            		if(maxTries > MAX_TRIES){
            			MktLog.d(LOG_TAG, "authorizationCommand : current tries =max tries");
            			loop = false;
                		break;
                	}
            		
            		try{
                		//sleep REQUEST_TRY_INTERNAL
                		Thread.sleep(REQUEST_TRY_INTERNAL);
                	}catch(Throwable th){
                		MktLog.e(LOG_TAG, "authorizationCommand : request.");
                	}
                	//currently,lack of a mechanism to make tries but, once.
                	authReqCtx.result = RestApiErrorCodes.NO_ERROR;
                    authReqCtx.httpStatusCode = HttpStatus.SC_OK;
                }
                break;
                case RestApiErrorCodes.SC_REQUEST_TIMEOUT_408:
                case RestApiErrorCodes.SC_INTERNAL_SERVER_ERROR_500:
                case RestApiErrorCodes.SC_BAD_GATEWAY_502:
                case RestApiErrorCodes.SC_SERVICE_UNAVAILABLE_503:
                case RestApiErrorCodes.SC_GATEWAY_TIMEOUT_504:{
                	if(refreshToken == isRefreshToken){
                		maxTries ++;
                		if(maxTries > MAX_TRIES){
                			MktLog.d(LOG_TAG, "authorizationCommand : current tries =max tries");
                    		if(isRefreshToken){
                    			MktLog.d(LOG_TAG, "authorizationCommand : according to error code,Perform OAuth authentication to get new tokens.");
                    			//Do not repeat it. we need to perform OAuth authentication to get new tokens.
                    			refreshToken = isRefreshToken = false;
                    			maxTries = 0;
                    			//clear refresh token.
                    			authReqCtx.lastRefreshToken = null;
                    			RCMProviderHelper.setRestRefreshToken(ctx, authReqCtx.mailboxId, null);
                    		}else{
                    			//perform OAuth authentication,max tries.
                    			loop = false;
                    			break;
                    		}
                    	}
                		
                	}else{
                		//channel change
                		MktLog.d(LOG_TAG, "authorizationCommand : refreshToken ="+refreshToken 
                				+ " isRefreshToken="+isRefreshToken
                				+ " set max tries=0");
                		maxTries = 0;
                		refreshToken = isRefreshToken;
                	}
                	
                	try{
                		//sleep REQUEST_TRY_INTERNAL
                		Thread.sleep(REQUEST_TRY_INTERNAL);
                	}catch(Throwable th){
                		MktLog.e(LOG_TAG, "authorizationCommand : request.");
                	}
                	//reset request
                	authReqCtx.result = RestApiErrorCodes.NO_ERROR;
                    authReqCtx.httpStatusCode = HttpStatus.SC_OK;
                }
                break;
                case RestApiErrorCodes.CLIENT_INVALID_ERROR:
                	//invalidate parameter for request,just break loop.
                	MktLog.d(LOG_TAG, "authorizationCommand : invalidate parameter for request,just break loop");
                	loop = false;
                	break;
                default:{
    	            //other error code
                	if(isRefreshToken){
                		MktLog.d(LOG_TAG, "authorizationCommand : according to error code,Perform OAuth authentication to get new tokens.");
                		//Do not repeat it. we need to perform OAuth authentication to get new tokens.likes change channel
                		refreshToken = isRefreshToken = false;
            			maxTries = 0;
            			//reset request
            			//clear refresh token.
            			authReqCtx.lastRefreshToken = null;
                    	authReqCtx.result = RestApiErrorCodes.NO_ERROR;
                        authReqCtx.httpStatusCode = HttpStatus.SC_OK;
                        RCMProviderHelper.setRestRefreshToken(ctx, authReqCtx.mailboxId, null);
                	}else{
                		//OAuth authentication other errors,do not repeat,just break loop.
                		//DO NOT repeat the request. Display error message.
                		MktLog.d(LOG_TAG, "authorizationCommand : OAuth authentication other error codes, DO NOT repeat the request. Display error message");
                		loop = false;
                	}
                }
                break;
                }
            }
            
            MktLog.d(LOG_TAG, "authorizationCommand after loop.");
            sProcessor.execute(new Command("OnAuthCompletion") {
                @Override
                public void run() {
                    onAuth(authReqCtx);
                }
            });
        } catch (Throwable th) {
            onAuthError("authorizationCommand : exception : " + th.toString(), authReqCtx);
        }
    }
    
    private static final void onAuthError(String message, final AuthRequestContext authReqCtx) {
        MktLog.e(LOG_TAG, "onAuthError: " + message);
        if (authReqCtx != null) {
            LOCK();
            try {
                RestSession session = get(authReqCtx.mailboxId);
                if (session == null || session.getState() != RestSessionState.AUTHORIZATION) {
                    return;
                }
                session.mAuthContext.lastAuthExpiresIn = 0;
                int status = RestApiErrorCodes.AUTHORIZATION_ERROR;
                session.onStateChange(RestSessionState.AUTHORIZATION_FAILED, status);
                for (RestRequest rest : pollPendentRequests(session.mFactoryId, session.mMailboxId)) {
                    sRequests.getAndDecrement();
                    requestToCompletion(rest, RestApiErrorCodes.AUTHORIZATION_ERROR, true);
                }
                UNLOCK("onAuthError:end");
                sendStateChangeNotification(session.mFactoryId, session.mMailboxId, RestSessionState.AUTHORIZATION_FAILED, status);
            } catch (Throwable th) {
                MktLog.e(LOG_TAG, ".onAuthError : exception : " + th.toString());
            } finally {
                UNLOCK_CHECK_UP("onAuthError");
            }
        }
    }
    
    /**
     * On authorization completion (executed in the command processor)
     * 
     * @param authReqCtx the authorization request
     */
    private static final void onAuth(final AuthRequestContext authReqCtx) {
        MktLog.d(LOG_TAG, "onAuth calling...");
        
        if (authReqCtx.mFactoryId != sFactoryId.get()) {
            MktLog.i(LOG_TAG, "onAuth : omitted, invalid factory.");
            return;
        }

        RestSession session = get(authReqCtx.mailboxId);
        LOCK();
        try {
            if (session == null || session.getState() == RestSessionState.DESTROYED) {
                UNLOCK("onAuth1");
                MktLog.w(LOG_TAG, authReqCtx.mailboxId + ".OnAuth : invalid session state.");
            } else {
                if (session.mAuthContext.authId > authReqCtx.authId) {
                    UNLOCK("onAuth2");
                    MktLog.w(LOG_TAG, session.mMailboxId + ".OnAuth : another authId (current="
                            + session.mAuthContext.authId + "; executed for " + authReqCtx.authId + ";");
                    return;
                }

                Context ctx = RingCentralApp.getContextRC();
                
                if (ctx == null) {
                    MktLog.e(LOG_TAG, ".OnAuth: context is null");
                }
                session.mAuthContext.lastAuthCmplElpsdTime = authReqCtx.completionTime;
                if (authReqCtx.result == RestApiErrorCodes.OK) {
                    session.mAuthContext.accessToken = authReqCtx.accessToken;
                    session.mAuthContext.refreshToken = authReqCtx.refreshToken;
                    session.mAuthContext.lastAuthExpiresIn = authReqCtx.expiresIn;
                    if (ctx != null) {
                       // RCMProviderHelper.setRestRefreshToken(ctx, session.mMailboxId, authReqCtx.refreshToken);
                        GeneralSettings.getSettings().setRefreshToken(authReqCtx.refreshToken);
                    }
                    session.onStateChange(RestSessionState.AUTHORIZED, RestApiErrorCodes.NO_ERROR);
                    PriorityBlockingQueue<RestRequest> q = new PriorityBlockingQueue<RestRequest>();
                    for (RestRequest rest : pollPendentRequests(session.mFactoryId, session.mMailboxId)) {
                        q.add(rest);
                    }
                    RestRequest rest = null;
                    while ((rest = q.poll()) != null) {
                        postRequestCommand(session.getRequestContext(rest));
                    }
                    UNLOCK("onAuth3");
                    sendStateChangeNotification(session.mFactoryId, session.mMailboxId, RestSessionState.AUTHORIZED,
                            RestApiErrorCodes.NO_ERROR);
                } else {
                    if (authReqCtx.result == RestApiErrorCodes.RESPONSE_HTTP_STATUS_ERROR
                            && (authReqCtx.httpStatusCode == HttpStatus.SC_UNAUTHORIZED 
                                    || authReqCtx.httpStatusCode == HttpStatus.SC_BAD_REQUEST)) {
                        authReqCtx.result = RestApiErrorCodes.AUTHORIZATION_ERROR;
                        if (ctx != null) {
                            RCMProviderHelper.setRestRefreshToken(ctx, session.mMailboxId, null);
                        }
                    }
                    session.mAuthContext.lastAuthExpiresIn = 0;
                    int status = authReqCtx.result;
                    session.onStateChange(RestSessionState.AUTHORIZATION_FAILED, status);
                    for (RestRequest rest : pollPendentRequests(session.mFactoryId, session.mMailboxId)) {
                        sRequests.getAndDecrement();
                        requestToCompletion(rest, RestApiErrorCodes.AUTHORIZATION_ERROR, true);
                    }
                    UNLOCK("onAuth4");
                    sendStateChangeNotification(session.mFactoryId, session.mMailboxId, RestSessionState.AUTHORIZATION_FAILED, status);
                }
            }
        } catch (Throwable th) {
            onAuthError("onAuth : exception : " + th.toString(), authReqCtx);
        } finally {
            UNLOCK_CHECK_UP("onAuth");
        }
    }
    
    /**
     * Turn the request to completion state (safe mode).
     * 
     * @param rest
     *            the request to turn to completion state.
     * 
     * @param result
     *            the result of completion, see {@link RESTStatusCode}
     * 
     * @param callOnCompletion
     *            tells if {@link #onCompletion()} invocation required
     */
    private static void requestToCompletion(final RestRequest rest, final int result,
            final boolean callOnCompletion) {
        try {
            if (result == RestApiErrorCodes.NO_ERROR) {
                sASyncRequests.getAndIncrement();
            } else {
                sASyncFailedRequests.getAndIncrement();
            }

            sExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    sThreads.incrementAndGet();
                    try {
                        rest.setCompletedState(result, callOnCompletion);
                    } finally {
                        sThreads.decrementAndGet();
                    }
                }
            });
        } catch (Throwable th) {
            MktLog.e(LOG_TAG, "requestToCompletion : exception : " + th.toString());
        }
    }
    
    /**
     * On request execution completed (executed in the command processor)
     * 
     * @param requestCtx
     *            the executed request context
     */
    private static final void onRequest(final RequestContext requestCtx) {
        RestRequest rest = requestCtx.request;
        
        if (sFactoryId.get() != rest.mFactoryId) {
            MktLog.e(LOG_TAG, rest.mLogTag + ".onRequest: invalid session state (factory id).");
            sRequests.getAndDecrement();
            requestToCompletion(rest, RestApiErrorCodes.INVALID_SESSION_STATE, true);
            return;
        }

        LOCK();
        try {
            RestSession session = RestSession.get(requestCtx.mailboxId);
            if (session == null) {
                UNLOCK("onRequest-session==null");
                MktLog.e(LOG_TAG, rest.mLogTag + ".onRequest: invalid session state (not found).");
                sRequests.getAndDecrement();
                requestToCompletion(rest, RestApiErrorCodes.INVALID_SESSION_STATE, true);
                return;
            }

            RestSessionState state = session.getState();
            switch (state) {
            case AUTHORIZATION:
            case AUTHORIZED:
            case AUTHORIZATION_FAILED:
                break;
            default:
                UNLOCK("onRequest-switch-default");
                MktLog.w(LOG_TAG, rest.mLogTag + ".onRequest: invalid session state (case)");
                sRequests.getAndDecrement();
                requestToCompletion(rest, RestApiErrorCodes.INVALID_SESSION_STATE, true);
                return;
            }
            
            //UNAUTHORIZED handle 401 error
            if (rest.getResult() == RestApiErrorCodes.RESPONSE_HTTP_STATUS_ERROR
                    && rest.mHttpCode == HttpStatus.SC_UNAUTHORIZED) {
            	//currently, AUTHORIZATION
                if (state == RestSessionState.AUTHORIZATION) {
                    rest.wasReAuthAttempt = false;
                    sPendingQueue.add(rest);
                    UNLOCK("onRequest-auth-error-auth");
                    rest.turnedToState(RestRequest.InternalState.PENDING_AUTHORIZATION);
                    MktLog.i(LOG_TAG, rest.mLogTag
                            + ".requestToCompletion auth error (session is AUTHORIZATION): pending request ");
                    return;
                }

                if (state == RestSessionState.AUTHORIZED) {
                    if (requestCtx.authId == session.mAuthContext.authId) {
                        if (rest.wasReAuthAttempt) {
                            UNLOCK("onRequest-auth-error-authd");
                            MktLog.e(LOG_TAG, rest.mLogTag
                                    + ".requestToCompletion auth error (session is AUTHORIZED): second auth failed");
                            sRequests.getAndDecrement();
                            requestToCompletion(rest, rest.getResult(), true);
                        } else {
                            rest.wasReAuthAttempt = true;
                            sPendingQueue.add(rest);
                            session.mAuthContext.authId = getSequenceNumber();
                            AuthRequestContext authCtx = session.getRequestContext();
                            session.onStateChange(RestSessionState.AUTHORIZATION, RestApiErrorCodes.NO_ERROR);
                            UNLOCK("onRequest-auth-error-authd");
                            sendStateChangeNotification(session.getFactoryId(), session.getMailBoxId(), RestSessionState.AUTHORIZATION, RestApiErrorCodes.NO_ERROR);
                            rest.turnedToState(RestRequest.InternalState.PENDING_AUTHORIZATION);
                            MktLog.i(
                                    LOG_TAG,
                                    rest.mLogTag
                                            + ".requestToCompletion auth error (session is AUTHORIZED): pending request and make re-authorization");
                            if(rest.mRestErrorResponse != null
                            		&& rest.mRestErrorResponse.errorCode != null
                            		&& RestApiErrorCodes.ERROR_CODE_TOKEN_INVALLID.equals(rest.mRestErrorResponse.errorCode)){
                            	//in this case, the Token is invalid, Client need to perform OAuth authentication to get new tokens.
                            	//clear refresh token.
                            	authCtx.lastRefreshToken = null;
                            	final Context ctx = RingCentralApp.getContextRC();
                            	RCMProviderHelper.setRestRefreshToken(ctx, authCtx.mailboxId, null);
                            }
                            postAuthorizationCommand(authCtx);
                        }
                    } else {
                        RequestContext newRequestContext = session.getRequestContext(rest);
                        UNLOCK("onRequest-auth-error-not-equal");
                        rest.turnedToState(RestRequest.InternalState.PENDING_EXECUTION);
                        MktLog.w(LOG_TAG, rest.mLogTag
                                + ".requestToCompletion auth error (session is AUTHORIZED) : make re-request");
                        postRequestCommand(newRequestContext);
                    }
                    return;
                }

                if (state == RestSessionState.AUTHORIZATION_FAILED) {
                    UNLOCK("onRequest-auth-err-failed");
                    MktLog.e(
                            LOG_TAG,
                            rest.mLogTag
                                    + ".requestToCompletion auth error (session is in AUTHORIZATION_FAILED state): return error");
                    sRequests.getAndDecrement();
                    requestToCompletion(rest, RestApiErrorCodes.AUTHORIZATION_ERROR, true);
                    return;
                }
            }

            UNLOCK("onRequest-finale");
        } finally {
            UNLOCK_CHECK_UP("onRequest");
        }
        sRequests.getAndDecrement();
        requestToCompletion(rest, rest.getResult(), true);
    }
    


    public void setMailboxId(long mailboxId) {
        this.mMailboxId = mailboxId;
    }

    
    private static void updateSessionMailBoxId(long oldMailboxId, long newMailboxId) {
        if (oldMailboxId == newMailboxId) {
            return;
        }

        MktLog.i(LOG_TAG, "updateSessionMailBoxId oldMailboxId=" + oldMailboxId + "newMailboxId=" + newMailboxId);
        LOCK();
        try {
            RestSession session = sSessions.get(oldMailboxId);
            if (session != null) {
                session.setMailboxId(newMailboxId);
                sSessions.put(newMailboxId, session);
//                sSessions.remove(oldMailboxId);
                MktLog.i(LOG_TAG, "updateSessionMailBoxId setMailboxId=" + newMailboxId);
            }

        } finally {
            UNLOCK("RestSession.createSession");
        }

    }

    private static boolean isMailboxIdHasSession(long mailboxId) {
        if (inValidMailBoxId(mailboxId)) {
            return false;
        }

        RestSession session = sSessions.get(mailboxId);
        // sSessions.clear();
        return session != null && (mailboxId == session.mMailboxId);
    }

    private static boolean inValidMailBoxId(long mailboxId) {
        if (mailboxId == 0) {
            MktLog.w(LOG_TAG, mailboxId + ".createSession: invalid mailboxId.");
            return true;
        }
        return false;
    }
    
    private static long getTokenExpirationInterval(){
    	return BASIC_TOKEN_EXPIRATION_INTERVAL;
    }
    /**
     * Makes a REST authorization.
     * 
     * @param authReqCtx
     *            the authorization context
     * @param refreshToken
     *            defines if needed make refresh token request, otherwise common authorization
     */
	private static void makeAuthorization(final AuthRequestContext authReqCtx, boolean refreshToken) {
    	if (authReqCtx.mFactoryId != sFactoryId.get()) {
            MktLog.w(LOG_TAG, "authorizationCommand : omitted, invalid factory.");
            authReqCtx.httpStatusCode = RestApiErrorCodes.CLIENT_INVALID_ERROR;
            return;
        }
    	
    	if (refreshToken) {
            MktLog.i(LOG_TAG, "makeAuthorization: refresh token");
        } else {
            MktLog.i(LOG_TAG, "makeAuthorization: common procedure");
        }
        
        sAuthRequests.getAndIncrement();
        
        final boolean USE_SIMPLEST_AUTHORIZATION = true;
      
        DefaultHttpClient client = null;
        HttpPost post = null;
        HttpResponse response = null;
        String logTag = new String(authReqCtx.mailboxId + ".oauth: ");
        try {
            client = authReqCtx.httpClient.getHttpClient();
            
            post = new HttpPost(authReqCtx.host + "/restapi/oauth/token");
            post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            post.addHeader("Connection",   "keep-alive");
            post.addHeader("Accept",        HttpUtils.JSON_CONTENT_TYPE);
            
            UsernamePasswordCredentials credentials =
                new UsernamePasswordCredentials(BUILD.REST_APPKEY, BUILD.REST_APPSECRET);
            if (USE_SIMPLEST_AUTHORIZATION) {
                BasicScheme scheme = new BasicScheme();
                post.addHeader(scheme.authenticate(credentials, post));
            } else {
                client.getCredentialsProvider().setCredentials(new AuthScope(null, -1), credentials);
            }

            if (!refreshToken) {
                post.setEntity(new StringEntity(authReqCtx.authString));
                try {
                    MktLog.d(
                            LOG_TAG,
                            new StringBuffer("> ")
                                    .append(logTag)
                                    .append('\n')
                                    .append(HttpUtils.logRequest(post, false, !LogSettings.ENGINEERING, "  "))
                                    .append('\n').append(authReqCtx.authLogString).append("\n < ")
                                    .append(logTag).toString());
                } catch (Throwable t) {
                }
            } else {
                   String endpoint_id = Utils.getUid();
            	   post.setEntity(new StringEntity("grant_type=refresh_token&refresh_token=" + authReqCtx.lastRefreshToken
                           + "&endpoint_id=" + endpoint_id
                           + "&refresh_token_ttl=" + getTokenExpirationInterval()));
                try {
                    String rTokenLog = "grant_type=refresh_token&refresh_token=XXX";
                    if (LogSettings.ENGINEERING) {
                        rTokenLog = "grant_type=refresh_token&refresh_token=" + authReqCtx.lastRefreshToken;
                    }
                    
                    MktLog.d(
                            LOG_TAG,
                            new StringBuffer("> ")
                                    .append(logTag)
                                    .append('\n')
                                    .append(HttpUtils.logRequest(post, false, !LogSettings.ENGINEERING, "  "))
                                    .append('\n').append(rTokenLog).append("\n < ")
                                    .append(logTag).toString());
                } catch (Throwable t) {
                }
            }

            MktLog.d(LOG_TAG, "makeAuthorization before post.");
            response = client.execute(post);
            MktLog.d(LOG_TAG, "makeAuthorization after post.");
            int statusCode = response.getStatusLine().getStatusCode();
            authReqCtx.httpStatusCode = statusCode;
            authReqCtx.completionTime = SystemClock.elapsedRealtime();
            MktLog.d(LOG_TAG, logTag + "response : HTTPS status code \"" + 
                    HttpUtils.getMsg(statusCode) + "\" from  \"" + authReqCtx.host + "\"");
           
            switch(statusCode){
            case RestApiErrorCodes.SC_OK_200:
            case RestApiErrorCodes.SC_CREATED_201:
            case RestApiErrorCodes.SC_NO_CONTENT_204:
            case RestApiErrorCodes.SC_MULTI_STATUS_207:{
                HttpEntity respEntity = response.getEntity();
                if (respEntity == null) {
                    MktLog.e(LOG_TAG, logTag + " : response is  null");
                    authReqCtx.result = RestApiErrorCodes.RESPONSE_INVALID_FORMAT;
                    return;
                }
                
                InputStream respInpStream = null;
                try {
                    respInpStream = respEntity.getContent();
                    Reader iReader = new InputStreamReader(respInpStream, "UTF-8");

                    if (LogSettings.ENGINEERING) {
                        // TODO: Turn to new logging stream
                        StringBuilder sBuilder = new StringBuilder();
                        BufferedReader bReader = new BufferedReader(iReader);
                        try {
                            for (String line; (line = bReader.readLine()) != null;) {
                                sBuilder.append(line).append('\n');
                            }
                        } finally {
                            try {
                                bReader.close();
                            } catch (IOException e) {
                            }
                        }
                        iReader = new StringReader(sBuilder.toString());
                    }
                    String mailboxId = "";
                    JsonReader jReader = new JsonReader(iReader);
                    jReader.beginObject();
                    while (jReader.hasNext()) {
                        String name = jReader.nextName();
                        if (name.equals("access_token")) {
                            authReqCtx.accessToken = jReader.nextString();
                        } else if (name.equals("refresh_token")) {
                            authReqCtx.refreshToken = jReader.nextString();
                        } else if (name.equals("expires_in")) {
                            authReqCtx.expiresIn = jReader.nextLong();
                        } else if (name.equals("owner_id")) {
                            mailboxId = jReader.nextString();
                        } else {
                            jReader.skipValue();
                        }
                    }
                    jReader.endObject();
                    
                    if (!(!TextUtils.isEmpty(authReqCtx.accessToken) && !TextUtils.isEmpty(authReqCtx.refreshToken) 
                            && (authReqCtx.expiresIn > 0))) {
                        MktLog.e(LOG_TAG, logTag + "Invalid authorization response");
                        authReqCtx.result = RestApiErrorCodes.RESPONSE_INVALID_FORMAT;
                    } else {
                        if (LogSettings.ENGINEERING) {
                            MktLog.d(LOG_TAG, logTag + "authorization success: access_token["
                                    + authReqCtx.accessToken + "] refresh_token[" + authReqCtx.refreshToken 
                                    + "] expires_in[" + authReqCtx.expiresIn + "]");
                            
                            MktLog.d(LOG_TAG, logTag + "authorization success (expires in " + authReqCtx.expiresIn + "s)");
                        }
                        long mailId = Long.valueOf(mailboxId);
                        long oldMailboxId = authReqCtx.mailboxId;
                        authReqCtx.mailboxId = mailId;
                        updateSessionMailBoxId(oldMailboxId, mailId);
                        GeneralSettings.getSettings().setCurrentUser(mailId);
                        CurrentUserSettings.getSettings(RingCentralApp.getContextRC()).setCurrentMailboxId(mailId);

//                        GeneralSettings.getSettings().setCurrentMailboxId(mailId);
                    }
                    
                    jReader.close();
                } catch (Throwable th) {
                    MktLog.e(LOG_TAG, logTag + "response exception : " + th.toString());
                    authReqCtx.result = RestApiErrorCodes.RESPONSE_PROCESSING_ERROR;
                } finally {
                    if (respInpStream != null) {
                        try {
                            respInpStream.close();
                            respInpStream = null;
                        } catch (Throwable th) {
                        }
                    }

                    if (respEntity != null) {
                        try {
                            respEntity.consumeContent();
                        } catch (Throwable th) {
                        }
                    }
                }
            }
            break;
            case RestApiErrorCodes.SC_BAD_REQUEST_400:{
                authReqCtx.result = RestApiErrorCodes.RESPONSE_HTTP_STATUS_ERROR;
                InputStream respInpStream = null;
                HttpEntity respEntity = null;
                try {
                    respEntity = response.getEntity();
                    respInpStream = respEntity.getContent();
                    Reader iReader = new InputStreamReader(respInpStream, "UTF-8");

                    if (LogSettings.ENGINEERING) {
                        // TODO: Turn to new logging stream
                        StringBuilder sBuilder = new StringBuilder();
                        BufferedReader bReader = new BufferedReader(iReader);
                        try {
                            for (String line; (line = bReader.readLine()) != null;) {
                                sBuilder.append(line).append('\n');
                            }
                        } finally {
                            try {
                                bReader.close();
                            } catch (IOException e) {
                            }
                        }
                        
                        MktLog.w(LOG_TAG, logTag + "Auth error response raw: " + sBuilder.toString());
                        iReader = new StringReader(sBuilder.toString());
                    }
                    
                    JsonReader jReader = new JsonReader(iReader);
                    jReader.beginObject();
                    while (jReader.hasNext()) {
                        String name = jReader.nextName();
                        if (name.equals("error")) {
                            authReqCtx.error = jReader.nextString();
                        } else if (name.equals("error_description")) {
                            authReqCtx.errorDescription = jReader.nextString();
                        } else {
                            jReader.skipValue();
                            MktLog.w(LOG_TAG, logTag + " auth error response unknown name : " + name);
                        }
                    }
                    jReader.endObject();
                    
                    StringBuffer sb = new StringBuffer("error:")
                        .append((TextUtils.isEmpty(authReqCtx.error) ? "null" : authReqCtx.error))
                        .append(" description:" )
                        .append((TextUtils.isEmpty(authReqCtx.errorDescription) ? "null" : authReqCtx.errorDescription));
                    
                    MktLog.e(LOG_TAG, logTag + "REST Auth Error Response : " + sb.toString());
                    
                    jReader.close();
                } catch (Throwable th) {
                    MktLog.e(LOG_TAG,
                            logTag + "an error in processing of REST Auth Error Response : " + th.toString(), th);
                } finally {
                    if (respInpStream != null) {
                        try {
                            respInpStream.close();
                            respInpStream = null;
                        } catch (Throwable th) {
                        }
                    }

                    if (respEntity != null) {
                        try {
                            respEntity.consumeContent();
                        } catch (Throwable th) {
                        }
                    }
                }
            }
            break;
            default:
            	authReqCtx.result = RestApiErrorCodes.RESPONSE_HTTP_STATUS_ERROR;
	            break;
            }
            
        } catch (Throwable th) {
            MktLog.e(LOG_TAG, logTag + "exception on request (\"" + authReqCtx.host + "\") : " + th.toString());
            authReqCtx.result = RestApiErrorCodes.CONNECTION_ERROR;
        } finally {
            try {
                if (post != null) {
                    HttpEntity h = post.getEntity();
                    if (h != null) {
                        h.consumeContent();
                    }
                }
            } catch (Throwable th) {
            }
            
            try {
                if (response != null) {
                    HttpEntity h = post.getEntity();
                    if (h != null) {
                        h.consumeContent();
                    }
                }
            } catch (Throwable th) {
            }
        }
    }
    
    /**
     * Makes a REST request. 
     * 
     * @param request the request context
     */
    private static void makeRequest(final RequestContext requestCtx) {
        
        sMakeRequests.getAndIncrement();
        
        if (requestCtx == null) {
            MktLog.e(LOG_TAG, "makeRequest : invalid request parameter.");
            return;
        }
        
        RestRequest rest = requestCtx.request;
        String logTag    = rest.mLogTag;
        
        if (!rest.isActive()) {
            MktLog.w(LOG_TAG, logTag + " makeRequest : request is not active");
            rest.setResult(RestApiErrorCodes.INVALID_REQUEST);
            rest.mHttpCode = RestApiErrorCodes.CLIENT_INVALID_ERROR;
            return;
        }

        rest.turnedToState(RestRequest.InternalState.EXECUTION);
        
        int executions = rest.totalExecutions.incrementAndGet();
        
        if (executions > 2) {
            sReqsCyclicWarn.getAndIncrement();
            MktLog.w(LOG_TAG, logTag + " makeRequest : execution number : " + executions);
        } else if (executions > 4) {
            MktLog.e(LOG_TAG, logTag + " makeRequest : execution number : " + executions);
            rest.setResult(RestApiErrorCodes.INVALID_REQUEST);
            rest.mHttpCode = RestApiErrorCodes.CLIENT_INVALID_ERROR;
            sReqsCyclicError.getAndIncrement();
            return;
        }
        
        
        String path = null;
        try {
            path = rest.getURI();
        } catch (Throwable th) {
            MktLog.e(LOG_TAG, logTag + " makeRequest : getURI : exception " + th.toString());
            rest.setResult(RestApiErrorCodes.INVALID_REQUEST);
            rest.mHttpCode = RestApiErrorCodes.CLIENT_INVALID_ERROR;
            return;
        }
        
        if (path == null) {
            try {
                path = requestCtx.host + rest.getPath();
            } catch (Throwable th) {
                MktLog.e(LOG_TAG, logTag + " makeRequest : getPath : exception " + th.toString());
                rest.setResult(RestApiErrorCodes.INVALID_REQUEST);
                rest.mHttpCode = RestApiErrorCodes.CLIENT_INVALID_ERROR;
                return;
            }
        }
        
        if (TextUtils.isEmpty(path)) {
            MktLog.e(LOG_TAG, logTag + " makeRequest : invalid URI");
            rest.setResult(RestApiErrorCodes.INVALID_REQUEST);
            rest.mHttpCode = RestApiErrorCodes.CLIENT_INVALID_ERROR;
            return;
        }
        
        // TODO: Validate final URI 
                
        DefaultHttpClient client = null;
        HttpRequestBase http = null;
        HttpResponse response = null;
        ByteArrayOutputStream baos = null;
        ByteArrayEntity att_byte = null;
        try {
            boolean requestBody = false;
            switch (rest.mMethod) {
            case GET:    http = new HttpGet(path);    break;
            case POST:   http = new HttpPost(path);   requestBody = true; break;
            case DELETE: http = new HttpDelete(path); break;
            default:     http = new HttpPut(path);    requestBody = true; break;
            }

            String body = null;
            if (requestBody) {
                try {
                    body = rest.getBody();
                } catch (Throwable th) {
                    MktLog.e(LOG_TAG, logTag + " makeRequest : getBody : exception " + th.toString());
                    rest.setResult(RestApiErrorCodes.INVALID_REQUEST);
                    rest.mHttpCode = RestApiErrorCodes.CLIENT_INVALID_ERROR;
                    return;
                }
            }

            http.addHeader("Authorization", "Bearer " + requestCtx.accessToken);
            http.addHeader("Accept",        HttpUtils.JSON_CONTENT_TYPE);
            
            try {
                rest.onHeaderForming(http);
            } catch (Throwable th) {
                MktLog.e(LOG_TAG, logTag + " makeRequest : onHeaderForming : exception " + th.toString());
                rest.setResult(RestApiErrorCodes.INVALID_REQUEST);
                rest.mHttpCode = RestApiErrorCodes.CLIENT_INVALID_ERROR;
                return;
            }

            if (!TextUtils.isEmpty(body)) {
            	((HttpEntityEnclosingRequestBase)http).setEntity(new StringEntity(body));
            } 
            
            if (rest.hasAttachment()) {
            	baos = rest.getAttachment();
				if (baos != null) {
					att_byte = new ByteArrayEntity(baos.toByteArray());
					
					try {
		                if (baos != null) {
		                	baos.close();
		            		baos = null;
		                }
		            } catch (Throwable th) {
		            }
					
					((HttpEntityEnclosingRequestBase)http).setEntity(att_byte);
				}
            }

            if (rest.mLogHttpRequest != RestRequest.LogHttp.NONE || LogSettings.ENGINEERING) {
                try {
                    MktLog.d(LOG_TAG, new StringBuffer("> Request ").append(logTag).append('\n')
                                    .append(HttpUtils.logRequest(http, (rest.mLogHttpRequest != 
                                        RestRequest.LogHttp.HEADER_ONLY), !LogSettings.ENGINEERING, "  "))
                                        .append("\n< Request ").append(logTag).toString());
                } catch (Throwable t) {
                }
            }
            
            client = requestCtx.httpClient.getHttpClient();

            response = client.execute(http);
            
            try {
                if (att_byte != null) {
                	att_byte.consumeContent();
                	att_byte = null;
                }
            } catch (Throwable th) {
            }
            
            rest.mHttpCode = response.getStatusLine().getStatusCode();
            requestCtx.completionTime = SystemClock.elapsedRealtime();
            
            if (rest.mHttpCode >= HttpStatus.SC_OK && rest.mHttpCode < HttpStatus.SC_MULTIPLE_CHOICES) {
            	//succeed
                MktLog.d(LOG_TAG, logTag + " successful http status code : " + HttpUtils.getMsg(rest.mHttpCode));
                HttpEntity respEntity = response.getEntity();
                
                if (respEntity == null) {
                    MktLog.d(LOG_TAG, logTag + "No response entity");
                    rest.setResult(RestApiErrorCodes.NO_ERROR);
                    MktLog.d(LOG_TAG, logTag + ".Completed");
                    return;
                }
                
                Header h = respEntity.getContentType();
                String contentType = null;     if (h != null) {contentType = h.getValue();}
                h = respEntity.getContentEncoding();
                String contentEncoding = null; if (h != null) {contentEncoding = h.getValue();}
                long length = respEntity.getContentLength();                
                
                InputStream respInpStream = null;
                try {
                    respInpStream = respEntity.getContent();
                    Reader iReader = null;
                    
                    if ((rest.mLogHttpResponse != RestRequest.LogHttp.NONE) || LogSettings.ENGINEERING) {
                        try {
                            MktLog.d(LOG_TAG, new StringBuffer("> Response Header ").append(logTag)
                                    .append("\n  Content-Type     : ").append(normalizedStringForDump(contentType))
                                    .append("\n  Content-Encoding : ").append(normalizedStringForDump(contentEncoding))
                                    .append("\n  Content-Length   : ").append(length)
                                    .append("\n< Response Header ").append(logTag).toString());
                        } catch (Throwable thl) {
                        }
                    }
                    
                    boolean streamedLogging = false;
                    if ((contentType != null) 
                        && (contentType.equals(HttpUtils.PLAIN_TEXT_CONTENT_TYPE) 
                                || contentType.equals(HttpUtils.JSON_CONTENT_TYPE)
                                || contentType.equals(HttpUtils.XML_APP_CONTENT_TYPE)
                                || contentType.equals(HttpUtils.XML_TEXT_CONTENT_TYPE)) 
                        && (rest.mLogHttpResponse == RestRequest.LogHttp.ALL)) {
                        streamedLogging = true;
                        if (!TextUtils.isEmpty(contentEncoding)) {
                            try {
                                iReader = new HttpResponseLogger(respInpStream, contentEncoding, MAX_BODY_LENGTH_FOR_LOGGING);
                            } catch (UnsupportedEncodingException ueex) {
                                MktLog.e(LOG_TAG, logTag + " onResponse unsupported encoding exception for : "
                                        + contentEncoding);
                                rest.setResult(RestApiErrorCodes.RESPONSE_INVALID_FORMAT);
                                return;
                            }
                        } else {
                            iReader = new HttpResponseLogger(respInpStream, MAX_BODY_LENGTH_FOR_LOGGING);
                        }
                    } else {
                        if (!TextUtils.isEmpty(contentEncoding)) {
                            try {
                                iReader = new InputStreamReader(respInpStream, contentEncoding);
                            } catch (UnsupportedEncodingException ueex) {
                                MktLog.e(LOG_TAG, logTag + " onResponse unsupported encoding rxcrption for : "
                                        + contentEncoding);
                                rest.setResult(RestApiErrorCodes.RESPONSE_INVALID_FORMAT);
                                return;
                            }
                        } else {
                            iReader = new InputStreamReader(respInpStream);
                        }
                    }
                    
                    boolean onResponseError = false;
                    try {
                        rest.onResponse(iReader, respInpStream, contentType, contentEncoding, length, response.getAllHeaders());
                    } catch (Throwable iox){
                        MktLog.e(LOG_TAG, logTag + ".onResponse exception : " + iox.toString());
                        onResponseError = true;
                        rest.setResult(RestApiErrorCodes.RESPONSE_PROCESSING_ERROR);
                    }
                    
                    if (streamedLogging) {
                        try {
                            StringBuffer sb = new StringBuffer("> Response Body ").append(logTag).append('\n');
                            if (iReader instanceof HttpResponseLogger) {
                                sb.append(normalizedStringForDump(((HttpResponseLogger) iReader).getContent()));
                            }
                            MktLog.d(LOG_TAG, sb.append("\n< Response Body ").append(logTag).toString());
                        } catch (Throwable thReader) {
                            MktLog.w(LOG_TAG, logTag + " logging exception : " + thReader.toString());
                        }
                    }
                    
                    if (onResponseError) {
                        return;
                    }
                    rest.setResult(RestApiErrorCodes.NO_ERROR);
                    MktLog.d(LOG_TAG, logTag + ".Completed");
                } catch (Throwable th) {
                    MktLog.e(LOG_TAG, logTag + " response exception : " + th.toString(), th);
                    rest.setResult(RestApiErrorCodes.RESPONSE_PROCESSING_ERROR);
                } finally {
                    if (respInpStream != null) {
                        try {
                            respInpStream.close();
                            respInpStream = null;
                        } catch (Throwable th) {
                        }
                    }

                    if (respEntity != null) {
                        try {
                            respEntity.consumeContent();
                        } catch (Throwable th) {
                        }
                    }
                }
            } else {
            	//error code.
                if (rest.mHttpCode == HttpStatus.SC_UNAUTHORIZED) {
                    MktLog.d(LOG_TAG, logTag + " response : error status code : " + HttpUtils.getMsg(rest.mHttpCode));
                } else {
                    MktLog.e(LOG_TAG, logTag + " response : error status code : " + HttpUtils.getMsg(rest.mHttpCode));
                }
                rest.setResult(RestApiErrorCodes.RESPONSE_HTTP_STATUS_ERROR);
                InputStream respInpStream = null;
                HttpEntity respEntity = null;
                try {
                    respEntity = response.getEntity();
                    respInpStream = respEntity.getContent();
                    Reader iReader = new InputStreamReader(respInpStream, "UTF-8");

                    if (LogSettings.ENGINEERING) {
                        // TODO: Turn to new logging stream
                        StringBuilder sBuilder = new StringBuilder();
                        BufferedReader bReader = new BufferedReader(iReader);
                        try {
                            for (String line; (line = bReader.readLine()) != null;) {
                                sBuilder.append(line).append('\n');
                            }
                        } finally {
                            try {
                                bReader.close();
                            } catch (IOException e) {
                            }
                        }
                        
                        if (rest.mHttpCode == HttpStatus.SC_UNAUTHORIZED) {
                            MktLog.v(LOG_TAG, logTag + "RestErrorResponse raw: " + sBuilder.toString());
                        } else {
                            MktLog.w(LOG_TAG, logTag + "RestErrorResponse raw: " + sBuilder.toString());
                        }
                        iReader = new StringReader(sBuilder.toString());
                    }
                    
                    rest.mRestErrorResponse = RestErrorResponse.onErrorResponse(iReader);
                    if (rest.mRestErrorResponse != null) {
                        if (rest.mHttpCode == HttpStatus.SC_UNAUTHORIZED) {
                            MktLog.v(LOG_TAG, logTag + " " + rest.mRestErrorResponse.toString());
                        } else {
                            MktLog.i(LOG_TAG, logTag + " " + rest.mRestErrorResponse.toString());
                        }
                    } else {
                        MktLog.e(LOG_TAG, logTag + "REST Error Response :is null");
                    }
                } catch (Throwable th) {
                    MktLog.e(LOG_TAG, logTag + "an error in processing of REST Error Response : " + th.toString(), th);
                } finally {
                    if (respInpStream != null) {
                        try {
                            respInpStream.close();
                            respInpStream = null;
                        } catch (Throwable th) {
                        }
                    }

                    if (respEntity != null) {
                        try {
                            respEntity.consumeContent();
                        } catch (Throwable th) {
                        }
                    }
                }
            }
        } catch (OutOfMemoryError ooe) {
        	MktLog.e(LOG_TAG, logTag + " exception on request : " + ooe.toString());
        	rest.setResult(RestApiErrorCodes.OUT_OF_MEMORY_ERROR);
        } catch (Exception th) {
        	MktLog.e(LOG_TAG, logTag + " exception on request : " + th.toString());
            rest.setResult(RestApiErrorCodes.CONNECTION_ERROR);
        } finally {
        
        	try {
                if (baos != null) {
                	baos.close();
            		baos = null;
                }
            } catch (Throwable th) {
            }
        	
        	try {
                if (att_byte != null) {
                	att_byte.consumeContent();
                	att_byte = null;
            		
                }
            } catch (Throwable th) {
            }
        	
        	System.gc();
        	
            try {
                if (http != null) {
                    if (http instanceof HttpEntityEnclosingRequestBase) {
                        HttpEntity h = ((HttpEntityEnclosingRequestBase)http).getEntity();
                        if (h != null) {
                            h.consumeContent();
                        }
                    }
                }
            } catch (Throwable th) {
            }
            
            try {
                if (response != null) {
                    HttpEntity h = response.getEntity();
                    if (h != null) {
                        h.consumeContent();
                    }
                }
            } catch (Throwable th) {
            }
        }
    }
    
    /**
     * Turns the session to another state. Must be called under #LOCK()
     * 
     * @param context
     *            the execution context
     * 
     * @param newState
     *            new state
     */
    void onStateChange(RestSessionState newState, int statusCode) {
        if (newState == null) {
            MktLog.e(LOG_TAG, mMailboxId + ".onStateChange: invalid parameters");
            return;
        }
        RestSessionState prevState;

        prevState = mState;
        if (prevState != RestSessionState.DESTROYED) {
            mState = newState;
            mStatusCode = statusCode;
        } else {
            newState = RestSessionState.DESTROYED;
        }

        if (newState == RestSessionState.AUTHORIZATION_FAILED) {
            sAuthFails.getAndIncrement();
            MktLog.e(LOG_TAG, mMailboxId + ".onStateChange: " + prevState.name() + " -> " + newState.name() + " ("
                    + RestApiErrorCodes.getMsg(statusCode) + ")");
        } else {
            MktLog.i(LOG_TAG, mMailboxId + ".onStateChange: " + prevState.name() + " -> " + newState.name());
        }

    }
    
    /**
     * Sends state change notification.
     * 
     * @param factoryId
     *            the factoryId of the session
     * @param mailboxId
     *            the mailboxId of the session
     * @param newState
     *            new state of the session to be notified about
     * @param statusCode
     *            the status code
     */
    private static void sendStateChangeNotification(long factoryId, long mailboxId, RestSessionState newState, int statusCode) {
        Context context = RingCentralApp.getContextRC();
        if (context == null) {
            MktLog.e(LOG_TAG, mailboxId + ".sendStateChangeNotification: broadcast sending failed: context is null.");
            return;
        }

        MktLog.d(LOG_TAG, mailboxId + ".sendStateChangeNotification: " + newState.name());
        MktLog.d(TAG, "=====sendStateChangeNotification= mailboxId="+mailboxId+"newState="+newState);

        try {
           
            Intent intent = new Intent(RestSessionStateChange.REST_SESSION_STATE_CHANGE_NOTIFICATION);
            intent.putExtra(RestSessionStateChange.REST_SESSION_STATE_CHANGE_TAG,
                    (Parcelable) new RestSessionStateChange(factoryId, mailboxId, newState, statusCode));
            context.sendBroadcast(intent);
            
        } catch (Throwable th) {
            MktLog.e(LOG_TAG, mailboxId + ".sendStateChangeNotification: broadcast sending failed: " + th.toString());
        }
    }
    
    /**
     * Obtain a sequence number that is unique across REST API implementation.
     */
    static long getSequenceNumber() {
        return sSequenceNumber.getAndIncrement();
    }

    /**
     * Returns the authorization body for "/restapi/oauth/token" path.
     * 
     * @param userName
     *            the user name
     * @param extension
     *            the optional extension
     * @param password
     *            the password
     * @param hidePassword
     *            defines if the password shall be hidden (body for logging purposes)
     * @return the authorization entity
     */
    private static String getAuthorizationBody(String userName, String extension, String password, boolean hidePassword) {
        StringBuffer sb = new StringBuffer("grant_type=password&username=").append(userName);
        if (!TextUtils.isEmpty(extension)) {
            sb.append("&extension=").append(extension);
        }
        sb.append("&password=");
        if (hidePassword) {
            sb.append("xxxxxxxx");
        } else {
            sb.append(URLEncoder.encode(password));
        }
        String endpoint_id = Utils.getUid();
        sb.append("&endpoint_id=" + endpoint_id);
        sb.append("&refresh_token_ttl=" + BASIC_TOKEN_EXPIRATION_INTERVAL);
        return sb.toString();
    }
    
    /**
     * Authorization context.
     */
    static class AuthContext {
        String authString, authLogString;
        String host;
        String accessToken, refreshToken;
        long lastAuthCmplElpsdTime = 0;
        long lastAuthExpiresIn = 0;
        long authId = 0;
    }
    
    /**
     * A context for authorization execution.
     */
    static class AuthRequestContext {
        long mailboxId, completionTime, expiresIn, mFactoryId, authId;
        String authString, authLogString, host, accessToken, refreshToken, lastRefreshToken;
        int result         = RestApiErrorCodes.NO_ERROR;
        int httpStatusCode = HttpStatus.SC_OK;
        String error;
        String errorDescription;
        RestHttpClient httpClient;
    }
    
    /**
     * A context for request executions. 
     */
    static class RequestContext {
        long mFactoryId, mailboxId, completionTime, authId;
        String accessToken, host;
        RestRequest request;
        RestHttpClient httpClient;
    }
    
    /**
     * Keeps command processor of the factory.
     */
    private static volatile CommandProcessor sProcessor = new CommandProcessor("RESTSessionService",
            android.os.Process.THREAD_PRIORITY_BACKGROUND, null);
    
    /**
     * Keeps pool of threads for requests executions.
     */
    private static volatile ExecutorService sExecutorService = Executors.newCachedThreadPool();
    
    /**
     * Keeps state of the session.
     */
    private volatile RestSessionState mState = RestSessionState.INITIAL;
    
    /**
     * Keeps status code, valid for {@link RestSessionState#AUTHORIZATION_FAILED} state.
     */
    private volatile int mStatusCode = RestApiErrorCodes.NO_ERROR;
    
    /**
     * Keeps mailboxId of the session.
     */
    private volatile long mMailboxId;
    
    /**
     * Keeps factoryId of the session.
     */
    private volatile long mFactoryId; 
    
    /**
     * Keeps HTTP client for the session.
     */
    private volatile RestHttpClient mHttpClient;
    
    /**
     * Keeps current authorization context.
     */
    private volatile AuthContext mAuthContext;
    
    /**
     * Keeps unique identifiers generator.
     */
    private static final AtomicLong sSequenceNumber = new AtomicLong(1);
    
    /**
     * Keeps active sessions.
     */
    private static final HashMap<Long, RestSession> sSessions = new HashMap<Long, RestSession>();
    
    /**
     * Common sync-primitive.
     */
    private static final ReentrantLock Lock = new ReentrantLock();
    
    /**
     * Keeps authorization waiting requests. 
     */
    private static volatile PriorityBlockingQueue<RestRequest> sPendingQueue = new PriorityBlockingQueue<RestRequest>();
    
    /**
     * Polls request from the pending queue by factory and mailbox identifiers.
     * 
     * @param factoryId
     *            the factoryId to select requests
     * @param mailboxId
     *            the mailboxId to select requests
     * 
     * @return the list of requests matched by <code>factoryId</code> and <code>mailboxId</code>
     */
    private static List<RestRequest> pollPendentRequests(long factoryId, long mailboxId) {
        ArrayList<RestRequest> list = new ArrayList<RestRequest>();
        int size = 0;
        boolean queueError = false;
        LOCK();
        try {
            for (RestRequest rest : sPendingQueue) {
                if (rest.mFactoryId == factoryId && rest.mMailboxId == mailboxId) {
                    list.add(rest);
                }
            }

            if (!list.isEmpty()) {
                for (RestRequest rest : list) {
                    if (!sPendingQueue.remove(rest)) {
                        MktLog.w(LOG_TAG, "pollPendentRequests : remove from queue failed for " + rest.toString());
                        queueError = true;
                    }
                }
            }
            
            size = sPendingQueue.size();

            if (size > 0) {
                MktLog.w(LOG_TAG, "pollPendentRequests : queue size still : " + size + " cleaning-up");
                
                sReqsPendentRequestCleans.getAndIncrement();
                
                ArrayList<RestRequest> dlist = new ArrayList<RestRequest>();
                boolean fullCleanUp = false;
                long cFactory = sFactoryId.get();
                
                for (RestRequest rest : sPendingQueue) {
                    if (rest.mFactoryId == factoryId && rest.mMailboxId == mailboxId) {
                        MktLog.e(LOG_TAG, "pollPendentRequests : platform error, request has not been removed " + rest.toString());
                        fullCleanUp = true;
                        break;
                    } else if (rest.mFactoryId != cFactory) {
                        MktLog.w(LOG_TAG, "pollPendentRequests : clean : invalid factory id : " + rest.toString());
                        dlist.add(rest);
                    } else {
                        RestSession session = get(rest.mMailboxId);
                        if (session == null || session.mState == RestSessionState.DESTROYED) {
                            MktLog.w(LOG_TAG, "pollPendentRequests : clean : invalid session : " + rest.toString());
                            dlist.add(rest);
                        }
                    }
                }

                if (fullCleanUp) {
                    MktLog.e(LOG_TAG, "pollPendentRequests : full clean-up.");
                    queueError = true;
                    sPendingQueue.clear();
                } else {
                    if (!dlist.isEmpty()) {
                        for (RestRequest rest : dlist) {
                            if (!sPendingQueue.remove(rest)) {
                                MktLog.e(LOG_TAG, "pollPendentRequests : error removing from queue again, full clean-up.");
                                sPendingQueue.clear();
                                queueError = true;
                                break;
                            } else {
                                MktLog.w(LOG_TAG, "pollPendentRequests : removed from queue as not valid : " + rest.toString());
                            }
                        }
                    }
                }
                
                size = sPendingQueue.size();
                if (size > 0) {
                    MktLog.w(LOG_TAG, "pollPendentRequests : queue size still : " + size + " after clean-up.");
                }
            }

            if (queueError) {
                sReqsPendentRequestErrors.getAndIncrement();
            }
            
            return list;
        } finally {
            UNLOCK("pollPendentRequests");
        }
    }
    
    /**
     * Keeps factory id (application session).
     */
    private static final AtomicLong sFactoryId = new AtomicLong(1);
    
    /**
     * Keeps number of active threads.
     */
    private static final AtomicLong sThreads = new AtomicLong(0);
    
    /**
     * Keeps number of active requests.
     */
    private static final AtomicLong sRequests = new AtomicLong(0);
    
    /**
     * Keeps number of sendRequest calls.
     */
    private static final AtomicLong sSendRequestsCalls = new AtomicLong(0);

    /**
     * Keeps number of authorize calls.
     */
    private static final AtomicLong sAuthorizeCalls = new AtomicLong(0);
    
    /**
     * Keeps number of create session calls.
     */
    private static final AtomicLong sCreateSessionCalls = new AtomicLong(0);

    /**
     * Keeps the number of auth.request calls.
     */
    private static final AtomicLong sAuthRequests = new AtomicLong(0);

    /**
     * Keeps the number of make requests calls.
     */
    private static final AtomicLong sMakeRequests = new AtomicLong(0);

    /**
     * Keeps the number of sync. failed requests.
     */
    private static final AtomicLong sSyncFailedRequests = new AtomicLong(0);
    
    /**
     * Keeps the number of async. failed requests.
     */
    private static final AtomicLong sASyncFailedRequests = new AtomicLong(0);

    /**
     * Keeps the number of async. successful requests.
     */
    private static final AtomicLong sASyncRequests = new AtomicLong(0);
    
    /**
     * Keeps the number of AUTH fails.
     */
    private static final AtomicLong sAuthFails = new AtomicLong(0);
    
    /**
     * Keeps the number of requests executed not applicable times (WARN level see makeRequest).
     */
    private static final AtomicLong sReqsCyclicWarn = new AtomicLong(0);
    
    /**
     * Keeps the number of requests executed not applicable times (ERR level see makeRequest).
     */
    private static final AtomicLong sReqsCyclicError = new AtomicLong(0);
    
    /**
     * Keeps the number of situations when there are pendent requests for authorization, extracted for current session, but some still exist.
     */
    private static final AtomicLong sReqsPendentRequestCleans = new AtomicLong(0);

    /**
     * Keeps the number of situations when clean-up of pendent requests happened.
     */
    private static final AtomicLong sReqsPendentRequestErrors = new AtomicLong(0);
    
    /**
     * Keeps an unique (next) sequence number across REST API implementation. 
     * 
     * AGREEMENT: MUST BE STARTED FROM SOME POSITIVE VALUE.
     */

    /**
     * Defines global logging tag.
     */
    private static final String LOG_TAG = "[RC]RESTSession";
    
    /**
     * Returns normalized string for logging.
     */
    static String normalizedStringForDump(String string) {
        if (string == null) {
            return "null";
        } else {
            return string;
        }
    }
    
    /**
     * Checks current network state {@see RestApiErrorCodes}.
     * 
     * Returns: {@link RestApiErrorCodes#NO_ERROR} or 
     * {@link RestApiErrorCodes#NETWORK_NOT_AVAILABLE} or
     * {@link RestApiErrorCodes#NETWORK_NOT_AVAILABLE_AIRPLANE_ON} or 
     * {@link RestApiErrorCodes#INVALID_SESSION_STATE}
     * 
     * @return current network state
     */
    private static int getNetworkState() {
        Context context = RingCentralApp.getContextRC();
        
        if (context == null) {
            MktLog.e(LOG_TAG, "getNetworkState : context is null.");
            return RestApiErrorCodes.INVALID_SESSION_STATE;
        }

        try {
            if (NetworkUtils.isAirplaneMode(context)) {
                return RestApiErrorCodes.NETWORK_NOT_AVAILABLE_AIRPLANE_ON;
            }
        } catch (Throwable th) {
            MktLog.e(LOG_TAG, "getNetworkState : exception on getting airplane mode : " + th.toString());
        }

        try {
            if (NetworkUtils.getNetworkState(context) == NetworkUtils.NetworkState.NONE) {
                return RestApiErrorCodes.NETWORK_NOT_AVAILABLE;
            }
        } catch (Throwable th) {
            MktLog.e(LOG_TAG, "getNetworkState : exception on getting airplane mode : " + th.toString());
        }

        return RestApiErrorCodes.NO_ERROR;
    }
    
    /**
     * Keeps lock/unlock counter for consistency check
     */
    private static final AtomicLong sLockNumber = new AtomicLong(0);
    
    /**
     * Acquires the common lock. 
     */
    private static void LOCK() {
        sLockNumber.getAndIncrement();
        Lock.lock();
    }

    /**
     * Attempts to release common lock. 
     */
    private static void UNLOCK(String msg) {
        sLockNumber.getAndDecrement();
        try {
            Lock.unlock();
        } catch (IllegalMonitorStateException imsex) {
            MktLog.e(LOG_TAG, msg + ": UNLOCK : unexpected exception : " + imsex.toString());
        }
    }

    /**
     * Attempts to release this lock, if unlock was successful there were missed standard unlocks.
     */
    private static void UNLOCK_CHECK_UP(String msg) {
        boolean thrown = false;
        try {
            Lock.unlock();
        } catch (IllegalMonitorStateException imsex) {
            thrown = true;
        }

        if (!thrown) {
            MktLog.e(LOG_TAG, msg + ": UNLOCK_CHECK_UP : missed unlock.");
        }
    }
    
    /**
     * Returns host.
     * 
     * @param ctx the execution context.
     * 
     * @return host URI
     */
    private static String getHost(Context ctx) {
        return "https://api.ringcentral.com";
    }
    
    /**
     * Helper function to set host URL for testing purpose.
     * 
     * @param host the host to be set for testing purpose.
     */
    static void _TEST_SET_HOST_URL(String host) {
        _sTestHost = host;
    }
    
    /**
     * Keeps test host.
     */
    private static String _sTestHost = null; 
    
    /**
     * Checks consistency for one existent session.
     * 
     * @param factoryId
     *            the factoryId of the session
     * @param mailboxId
     *            the mailboxId of the session
     * @param state
     *            the expected state of the session
     * @return <code>true</code> on success, otherwise <code>false</code>
     */
    static boolean checkConsistencyForOneSession(long factoryId, long mailboxId, RestSessionState state) {
        long threadsNumber = sThreads.get();
        long requestsNumber = sRequests.get();
        
        long procCommands = sProcessor.getNumberOfEnqueuedCommands();
        if (sProcessor.getCommandIdUnderExecution() != 0) {
            procCommands++;
        }
        
        LOCK();
        try {
            boolean success = true;
            if (threadsNumber != 0) {
                MktLog.e(LOG_TAG, "checkConsistencyForOneSession : threads number is " + threadsNumber);
                success = false;
            }
            
            if (procCommands > 1) {
                MktLog.e(LOG_TAG, "checkConsistencyForOneSession : commands number is " + procCommands);
                success = false;
            }
            
            if (sSessions.size() != 1) {
                MktLog.e(LOG_TAG, "checkConsistencyForOneSession : sessions number is " + sSessions.size());
                success = false;
            }
            
            if (success) {
                for (RestSession session : sSessions.values()) {
                    if (session.getFactoryId() != factoryId) {
                        MktLog.e(LOG_TAG, "checkConsistencyForOneSession : session invalid factoryId : " 
                                + session.getFactoryId() + " : expected " + factoryId);
                        success = false;
                    }
                    
                    if (session.getMailBoxId() != mailboxId) {
                        MktLog.e(LOG_TAG, "checkConsistencyForOneSession : session invalid mailboxId : " 
                                + session.getMailBoxId() + " : expected " + mailboxId);
                        success = false;
                    }
                    
                    if (session.getState() != state) {
                        MktLog.e(LOG_TAG, "checkConsistencyForOneSession : session invalid state : " 
                                + session.getState().name() + " : expected " + state.name());
                        success = false;
                    }
                }
            }
            
            if (requestsNumber != 0) {
                MktLog.e(LOG_TAG, "checkConsistencyForOneSession : requests number is " + requestsNumber);
                success = false;
            }
            
            if (sPendingQueue.size() != 0) {
                MktLog.e(LOG_TAG, "checkConsistencyForOneSession : pending requests number is " + sPendingQueue.size());
                success = false;
                    for (RestRequest rest : sPendingQueue) {
                        MktLog.e(LOG_TAG, "  > "+ rest.toString());
                    }
            }
            MktLog.i(LOG_TAG, "checkConsistencyForOneSession :" + success);
            UNLOCK("checkConsistencyForOneSession");
            return success;
        } finally {
            UNLOCK_CHECK_UP("checkConsistencyForOneSession");
        }
    }
    
    /**
     * Dump of state and accumulated statistics.
     * 
     * @return dump of state and accumulated statistics
     */
    public static String dumpState() {
        int sessions = 0;
        int pendings = 0;

        LOCK();
        try {
            sessions = sSessions.size();
            pendings = sPendingQueue.size();
            UNLOCK("dumpState");
        } finally {
            UNLOCK_CHECK_UP("dumpState");
        }
        long procCommands = sProcessor.getNumberOfEnqueuedCommands();
        if (sProcessor.getCommandIdUnderExecution() != 0) {
            procCommands++;
        }        
        
        StringBuffer sb = new StringBuffer().append(LOG_TAG).append(" dump:")
                .append(" factoryId:").append(sFactoryId.get())
                .append(" active (threads:").append(sThreads.get()).append(" reqs:").append(sRequests.get())
                .append(" cmds:").append(procCommands).append(")\n")
                .append(" sessions:").append(sessions).append(" pendings:").append(pendings)
                .append(" pqueueCleans:").append(sReqsPendentRequestCleans.get())
                .append(" pqueueErrors:").append(sReqsPendentRequestErrors.get())
                .append(" cyclic-reqs-warn:").append(sReqsCyclicWarn.get())
                .append(" cyclic-reqs-err:").append(sReqsCyclicError.get()).append('\n')
                .append(" sendReqsCalls:").append(sSendRequestsCalls.get())
                .append(" authCalls:").append(sAuthorizeCalls.get())
                .append(" createCalls:").append(sCreateSessionCalls.get())
                .append(" authReqs:").append(sAuthRequests.get())
                .append(" makeReqs:").append(sMakeRequests.get()).append('\n')
                .append(" syncFailedReqs:").append(sSyncFailedRequests.get())
                .append(" asyncFailedReqs:").append(sASyncFailedRequests.get())
                .append(" asyncSuccessReqs:").append(sASyncRequests.get()).append('\n')
                .append(" lockCounter:").append(sLockNumber.get())
                .append(" authFails:").append(sAuthFails.get()).append('\n');
        return sb.toString();
    }
    
    /**
     * Static initializer that shall be called at application start-up to force static loading. 
     */
    public static void initStatic() {
        MktLog.i(LOG_TAG, "initStatic : " + dumpState());
    }
}
