/** 
 * Copyright (C) 2013, RingCentral, Inc.
 *  
 * All Rights Reserved.
 */
package com.example.nickgao.utils;

import android.util.SparseArray;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;

/**
 * Helper HTTP routines and definitions.
 */
public final class HttpUtils {
    
	public static final String CONTENT_TYPE_NAME       		= "Content-Type";
    public static final String JSON_CONTENT_TYPE       		= "application/json";
    public static final String MIXED_CONTENT_TYPE       	= "multipart/mixed";
    public static final String PLAIN_TEXT_CONTENT_TYPE 		= "text/plain";
    public static final String XML_TEXT_CONTENT_TYPE   		= "text/xml";
    public static final String XML_APP_CONTENT_TYPE    		= "application/xml";
    public static final String STREAM_CONTENT_TYPE      	= "application/octet-stream";
    public static final String TRANSFER_ENCODING 			= "Transfer-Encoding";
    public static final String CHUNKED						= "chunked";
    public static final String IF_NONE_MATCH = "If-None-Match";

    /**
     * Return dump of a HTTP request for logging purposes.
     * 
     * @param httpRequest
     *            the request to be logged
     * @param includeBody
     *            defines if body shall be logged
     * @param hideAuthHeader
     *            defines if the authorization header shall be hidden
     * @param indent
     *            indent for logging lines
     * @return the dump of the request
     */
    public static String logRequest(HttpRequest httpRequest, boolean includeBody, boolean hideAuthHeader, String indent) {
        if (httpRequest == null || indent == null) {
            return "Request is null";
        }
        
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(indent).append(httpRequest.getRequestLine().toString()).append('\n');
            for (Header header : httpRequest.getAllHeaders()) {
                String name = header.getName();
                String value = header.getValue();
                if ("Authorization".equals(name) && hideAuthHeader) {
                    value = "XXXXX";
                }
                sb.append(indent).append(name).append(" : ").append(value).append('\n');
            }

            if (includeBody) {
                sb.append(indent).append('\n');
                HttpEntity entity = null;
                if (httpRequest instanceof HttpEntityEnclosingRequest)
                    entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();

                byte[] data;
                if (entity == null) {
                    data = new byte[0];
                } else {
                    data = EntityUtils.toByteArray(entity);
                }
                sb.append(indent).append(new String(data));
            }
        } catch (Throwable th) {
            sb.append("EXCEPTION:" + th.toString());
        }
        return sb.toString();
    }
    
    /**
     * Keeps messages definition of status codes.
     */
    private static SparseArray<String> sHttpCodes = new SparseArray<String>();
    
    /**
     * Static initializer for HTTP status codes enumeration.
     */
    static {
        sHttpCodes.put(200, "200 OK");
        
        sHttpCodes.put(201, "201 Created");
        sHttpCodes.put(202, "202 Accepted");
        sHttpCodes.put(203, "203 Non-Authoritative Information");
        sHttpCodes.put(204, "204 No Content");
        sHttpCodes.put(205, "205 Reset Content");
        sHttpCodes.put(206, "206 Partial Content");
        
        sHttpCodes.put(400, "400 Bad Request");
        sHttpCodes.put(401, "401 Unauthorized");
        sHttpCodes.put(402, "402 Payment Required");
        sHttpCodes.put(403, "403 Forbidden");
        sHttpCodes.put(404, "404 Not Found");
        sHttpCodes.put(405, "405 Method Not Allowed");
        sHttpCodes.put(406, "406 Not Acceptable");
        sHttpCodes.put(407, "407 Proxy Authentication Required");
        sHttpCodes.put(408, "408 Request Timeout");
        
        
        sHttpCodes.put(500, "500 Internal Server Error");
        sHttpCodes.put(501, "501 Not Implemented");
        sHttpCodes.put(502, "502 Bad Gateway");
        sHttpCodes.put(503, "503 Service Unavailable");
        sHttpCodes.put(504, "504 Gateway Timeout");
        sHttpCodes.put(505, "505 HTTP Version Not Supported");
    }

    /**
     * Returns a user-friendly message described the status code for using in logging.
     *  
     * @param statusCode the HTTP status code
     * 
     * @return a message described the status code
     */
    public static String getMsg(int statusCode) {
        String msg = sHttpCodes.get(statusCode);
        if (msg == null) {
            return new String(statusCode + "(UNKNOWN HTTP STATUS CODE)");
        } else {
            return msg;
        }
    }
    
    /**
     * Helper reader for logging.
     */
    public static class HttpResponseLogger extends InputStreamReader {
        /**
         * Keeps builder for accumulating log.
         */
        private StringBuilder sBuilder = new StringBuilder();
        
        /**
         * Defines max. length of the builder.
         */
        private long maxLen;

        /**
         * Constructs a new reader.
         * 
         * @param in
         *            the InputStream from which to read characters.
         * @param enc
         *            identifies the character converter to use
         * @param maxLen
         *            defines max. length to be logged
         * @throws UnsupportedEncodingException
         *             if the encoding specified by enc cannot be found.
         */
        public HttpResponseLogger(InputStream in, String enc, long maxLen) throws UnsupportedEncodingException {
            super(in, enc);
            this.maxLen = maxLen;
        }
        
        /**
         * Constructs a new reader.
         * 
         * @param in
         *            the InputStream from which to read characters.
         * @param maxLen
         *            defines max. length to be logged
         */
        public HttpResponseLogger(InputStream in, long maxLen) {
            super(in);
            this.maxLen = maxLen;
        }

        /**
         * Retrieves logged content.
         * 
         * @return the logged content.
         */
        public String getContent() {
            return sBuilder.toString();
        }
        
        @Override
        public void close() throws IOException {
            super.close();
        }

        @Override
        public String getEncoding() {
            return super.getEncoding();
        }

        @Override
        public int read() throws IOException {
            int r = super.read();
            if (r != -1 && (sBuilder.length() < maxLen)) {
                sBuilder.append((char)r);
            }
            return r;
        }

        @Override
        public int read(char[] buffer, int offset, int length) throws IOException {
            int r = super.read(buffer, offset, length);
            if (r != -1 && (sBuilder.length() < maxLen)) {
                sBuilder.append(buffer, offset, r);
            }
            return r;
        }

        @Override
        public boolean ready() throws IOException {
            return super.ready();
        }

        @Override
        public void mark(int readLimit) throws IOException {
            super.mark(readLimit);
        }

        @Override
        public boolean markSupported() {
            return super.markSupported();
        }

        @Override
        public int read(char[] buf) throws IOException {
            int r = super.read(buf);
            if (r != -1 && (sBuilder.length() < maxLen)) {
                sBuilder.append(buf, 0, r);
            }
            return r; 
        }

        @Override
        public int read(CharBuffer target) throws IOException {
            int r = super.read(target);
            if (r != -1 && (sBuilder.length() < maxLen)) {
                sBuilder.append(target, 0, r);
            }
            return r;
        }

        @Override
        public void reset() throws IOException {
            super.reset();
        }

        @Override
        public long skip(long charCount) throws IOException {
            return super.skip(charCount);
        }
    }
}
