/** 
 * Copyright (C) 2012, RingCentral, Inc.
 *  
 * All Rights Reserved.
 */
package com.example.nickgao.network;


import java.io.IOException;

import com.google.agson.stream.JsonReader;
import com.example.nickgao.logging.MktLog;

public class RestRequestNavigation {
    /**
     * The canonical URI of the first page.
     */
    public String firstPage;
    
    /**
     * The canonical URI of the last page.
     */
    public String lastPage;

    /**
     * The canonical URI of the previous page.
     */
    public String previousPage;

    /**
     * The canonical URI of the last page.
     */
    public String nextPage;
    
    /**
     * Parses "navigation" element and returns parsed data as <code>RestRequestNavigation</code> instance.
     * 
     * @param jReader
     *            the reader
     * @return <code>RestRequestNavigation</code> instance.
     * @throws IOException
     *             in case of any IO failures
     */
    public static RestRequestNavigation onNavigationElementResponse(JsonReader jReader) throws IOException {
        RestRequestNavigation ret = new RestRequestNavigation();
        try {
            jReader.beginObject();
            while (jReader.hasNext()) {
                String name = jReader.nextName();
                if (name.equals("firstPage")) {
                    ret.firstPage = getURI(jReader);
                } else if (name.equals("lastPage")) {
                    ret.lastPage = getURI(jReader);
                } else if (name.equals("previousPage")) {
                    ret.previousPage = getURI(jReader);
                } else if (name.equals("nextPage")) {
                    ret.nextPage = getURI(jReader);
                } else {
                    jReader.skipValue();
                    MktLog.w(TAG, " Unknown item " + name);
                }
            }
            jReader.endObject();
        } catch (Throwable th) {
            MktLog.e(TAG, ".onNavigationElementResponse : exception : " + th.toString(), th);
            throw new IOException(TAG + " : " + th.toString());
        }
        MktLog.v(TAG, ret.toString());
        return ret;
    }
    
    /**
     * Reads URI.
     * 
     * @param jReader the reader
     * @return URI
     * @throws IOException in case of errors
     */
    private static String getURI(JsonReader jReader) throws IOException {
        String ret = null;
        jReader.beginObject();
        while (jReader.hasNext()) {
            String name = jReader.nextName();
            if (name.equals("uri")) {
                ret = jReader.nextString();
            } else {
                jReader.skipValue();
                MktLog.w(TAG, " Unknown item under page " + name);
            }
        }
        jReader.endObject();
        return ret;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Navigation: \n")
        .append("  firstPage:").append(getLogString(firstPage)).append('\n')
        .append("  lastPage:").append(getLogString(lastPage)).append('\n')
        .append("  previousPage:").append(getLogString(previousPage)).append('\n')
        .append("  nextPage:").append(getLogString(nextPage));
        return sb.toString();
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
     * Keeps logging tag.
     */
    private static final String TAG = "[RC]RestRequestNavigation";
}
