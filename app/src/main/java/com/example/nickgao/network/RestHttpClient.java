/** 
 * Copyright (C) 2012-2013, RingCentral, Inc. 
 * 
 * All Rights Reserved.
 */

package com.example.nickgao.network;

import android.text.TextUtils;

import com.example.nickgao.logging.BUILD;
import com.example.nickgao.logging.Brands;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.utils.HttpUtils;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * Default implementation of a client-side RSET HTTP connection.
 */
class RestHttpClient {
    /**
     * Define logging tag.
     */
    private static final String TAG = "[RC]RestHttpClient";

    /**
     * Keeps HTTP client.
     */
    private RCMHttpClient client;
    
    /**
     * Keeps the connection manger for the client.
     */
    private ClientConnectionManager manager;
    
    /**
     * Keeps mailboxId of the session the client serves.
     */
    private long mailboxId;
    
    /**
     * Keeps logging tag for the client.
     */
    private String logTag;
    
    /**
     * Hidden constructor.
     * 
     * @param mailboxId  mailboxId of the session the client serves
     */
    private RestHttpClient(long mailboxId) {
        logTag = new String(TAG + "(mailboxId=" + mailboxId + ")");
        this.mailboxId = mailboxId;
    }
    
    /**
     * Returns <code>DefaultHttpClient</code> for making a HTTP request.
     * 
     * @return <code>DefaultHttpClient</code> for making a HTTP request
     */
    DefaultHttpClient getHttpClient() {
        cleanup();
        return client;
    }
    
    /**
     * Defines the timeout in milliseconds until a connection is established. 
     */
    private static final int CONNECTION_TIMEOUT = 30 * 1000;
    
    /**
     * Defines the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.
     */
    private static final int DATA_WAITING_TIMEOUT = 120 * 1000;
    
    /**
     * Defines the idle time in minutes of connections to be closed 
     */
    private static final long IDLE_CONNECTIONS_CLEAN_UP = 10 * 60;
    
    /**
     * Defines the maximum number of connections allowed.
     */
    private static final int MAX_CONNECTIONS = 32;
    
    /**
     * Returns a HTTP client for REST connections.
     * 
     * @param mailboxId
     *            mailboxId of the session the client will serve
     * 
     * @return the client instance.
     */
    static RestHttpClient getClient(long mailboxId) {
        try {
            HttpParams params = new BasicHttpParams();
            params.setParameter(HttpUtils.TRANSFER_ENCODING, HttpUtils.CHUNKED);
            
            ConnManagerParams.setMaxTotalConnections(params, MAX_CONNECTIONS);
            
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            
            HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, DATA_WAITING_TIMEOUT);
            
            ConnManagerParams.setTimeout(params, CONNECTION_TIMEOUT);

            ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {
                @Override
                public int getMaxForRoute(HttpRoute httproute) {
                    return MAX_CONNECTIONS;
                }
            });

            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            
            RestHttpClient restHttpClient = new RestHttpClient(mailboxId); 
            restHttpClient.manager = new ThreadSafeClientConnManager(params, schemeRegistry);
            
            if (restHttpClient.manager == null)  {
                MktLog.e(TAG, "getClient : manager is null.");
                return null;
            }
            
            restHttpClient.client = new RCMHttpClient(restHttpClient.manager, params);
            
            if (restHttpClient.client == null)  {
                MktLog.e(TAG, "getClient : client is null.");
                
                return null;
            }
            
            setUserAgent(restHttpClient.client);
            
            return restHttpClient;
        } catch (Throwable th) {
            MktLog.e(TAG, "getClient : exception : " + th.toString());
        }
        return null;
    }
    
    /**
     * Set User-Agent header.
     * 
     * @param client
     *            the HTTP client to set User-Agent header.
     */
    private static void setUserAgent(DefaultHttpClient client) {
        try {
            StringBuffer newUAsb = new StringBuffer("RCMobile");
            if (!TextUtils.isEmpty(BUILD.VERSION_NAME)) {
                newUAsb.append("/").append(BUILD.VERSION_NAME);
            }

            ArrayList<String> comment = new ArrayList<String>();

            switch (BUILD.BRAND) {
            case Brands.ATT_BRAND:
                comment.add("OfficeAtHand");
                break;
            case Brands.ROGERS_BRAND:
                comment.add("RogersHostedIPVoice");
                break;
            default:
                comment.add("RingCentral");
                break;

            }

            StringBuffer osSb = new StringBuffer("Android");
            if (!TextUtils.isEmpty(android.os.Build.VERSION.RELEASE)) {
                osSb.append("/").append(android.os.Build.VERSION.RELEASE);
            }
            comment.add(osSb.toString());

            if (!TextUtils.isEmpty(BUILD.SVN_REVISION)) {
                comment.add("rev." + BUILD.SVN_REVISION);
            }

            if (!comment.isEmpty()) {
                newUAsb.append(" (");
                boolean first = true;
                for (String s : comment) {
                    if (first) {
                        first = false;
                        newUAsb.append(s);
                    } else {
                        newUAsb.append("; ");
                        newUAsb.append(s);
                    }
                }
                newUAsb.append(")");
            }

            String curUA = HttpProtocolParams.getUserAgent(client.getParams());
            if (!TextUtils.isEmpty(curUA)) {
                newUAsb.append(" ").append(curUA);
            }
            String newUA = newUAsb.toString();
            MktLog.i(TAG, "setUserAgent : " + newUA);
            HttpProtocolParams.setUserAgent(client.getParams(), newUA);
        } catch (Throwable th) {
            MktLog.e(TAG, "setUserAgent : error : " + th.toString());
        }
    }

    /**
     * Cleans-up expired and idle (for long time) connections.
     */
    void cleanup() {
        try {
            if (manager != null) {
                manager.closeExpiredConnections();
                manager.closeIdleConnections(IDLE_CONNECTIONS_CLEAN_UP, TimeUnit.SECONDS);
            }
        } catch (Throwable th) {
            MktLog.e(logTag, "cleanup : exception : " + th.toString());
        }
    }
    
    /**
     * Shutdown all connections (instances at HttpClient) and release memory.
     */
    void shutdown() {
        MktLog.i(logTag, "shutdown(mailboxId=" + mailboxId + ")");
        try {
            if (manager != null) {
                cleanup();
                manager.shutdown();
                manager = null;
                client = null;
            }
        } catch (Throwable th) {
            MktLog.e(logTag, "shutdown : exception : " + th.toString());
        }
    }
}
