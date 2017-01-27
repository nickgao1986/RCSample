/** 
 * Copyright (C) 2012, RingCentral, Inc.
 *  
 * All Rights Reserved.
 */
package com.example.nickgao.network;

import com.example.nickgao.logging.MktLog;
import com.google.agson.stream.JsonReader;

import java.io.IOException;

public class RestRequestPaging {
    /**
     * Current page ordinal number, by default starts with 1, may be null (0) if non-existent page was requested.
     */
    public int page;
    
    /**
     * Current page size. Default value is 100. Maximum value is 1000. If perPage value in request is greater than 1000,
     * max. value (1000) is applied instead.
     */
    public int perPage;
    
    /**
     * The zero-based number of the first element on current page.
     */
    public int pageStart;
    
    /**
     * The zero-based index of the last element on current page.
     */
    public int pageEnd;
    
    /**
     * The total number of pages in a data-set.
     */
    public int totalPages;
    
    /**
     * The total number of elements in a data-set.
     */
    public int totalElements;

    /**
     * Parses "paging" element and returns parsed data as <code>RestRequestPaging</code> instance.
     * 
     * @param jReader
     *            the reader
     * @return <code>RestRequestPaging</code> instance.
     * @throws IOException
     *             in case of IO errors
     */
    public static RestRequestPaging onPagingElementResponse(JsonReader jReader) throws IOException {
        RestRequestPaging ret = new RestRequestPaging();
        try {
            jReader.beginObject();
            while (jReader.hasNext()) {
                String name = jReader.nextName();
                if (name.equals("page")) {
                    ret.page = jReader.nextInt();
                } else if (name.equals("totalPages")) {
                    ret.totalPages = jReader.nextInt();
                } else if (name.equals("perPage")) {
                    ret.perPage = jReader.nextInt();
                } else if (name.equals("totalElements")) {
                    ret.totalElements = jReader.nextInt();
                } else if (name.equals("pageStart")) {
                    ret.pageStart = jReader.nextInt();
                } else if (name.equals("pageEnd")) {
                    ret.pageEnd = jReader.nextInt();
                } else {
                    jReader.skipValue();
                    MktLog.w(TAG, " Unknown item " + name);
                }
            }
            jReader.endObject();
        } catch (Throwable th) {
            MktLog.e(TAG, ".onPagingElementResponse processing : exception : " + th.toString(), th);
            throw new IOException(TAG + " : " + th.toString());
        }
        MktLog.v(TAG, ret.toString());
        return ret;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Paging: ")
        .append(" page:").append(page)
        .append(" totalPages:").append(totalPages)
        .append(" perPage:").append(perPage)
        .append(" totalElements:").append(totalElements)
        .append(" pageStart:").append(pageStart)
        .append(" pageEnd:").append(pageEnd);
        return sb.toString();
    }
    
    /**
     * Keeps logging tag.
     */
    private static final String TAG = "[RC]RestRequestPaging";
}
