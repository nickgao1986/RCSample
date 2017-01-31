package com.example.nickgao.contacts;

import com.example.nickgao.service.request.RcRestRequest;

import org.apache.http.HttpMessage;
import org.apache.http.protocol.HTTP;

import java.lang.reflect.Type;

/**
 * Created by nick.gao on 1/31/17.
 */

public class UpdateCloudFavoriteRequest<T> extends RcRestRequest<T> {
    private String mRequestBody;


    public UpdateCloudFavoriteRequest(int requestId, Type responseType, HttpMethod method, String logTag, String requestBody) {
        super(requestId, responseType, method, logTag);
        mRequestBody = requestBody;
    }

    @Override
    public String getBody() {
        return mRequestBody;
    }

    @Override
    public void onHeaderForming(HttpMessage request) {
        super.onHeaderForming(request);
        request.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
        request.addHeader("Accept", "application/json;charset=UTF-8");
    }

    @Override
    public boolean isBodyConvertToUTF8() {
        return true;
    }
}
