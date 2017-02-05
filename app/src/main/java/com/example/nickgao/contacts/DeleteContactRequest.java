package com.example.nickgao.contacts;

import com.example.nickgao.service.request.RcRestRequest;

import org.apache.http.HttpMessage;
import org.apache.http.protocol.HTTP;

import java.lang.reflect.Type;

/**
 * Created by nick.gao on 1/30/17.
 */

public class DeleteContactRequest<T> extends RcRestRequest<T> {

    public DeleteContactRequest(int requestId, Type responseType, HttpMethod method, String logTag) {
        super(requestId, responseType, method, logTag);
    }

    @Override
    public void onHeaderForming(HttpMessage request) {
        super.onHeaderForming(request);
        //request.addHeader("Content-Type", "application/json");
        request.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
    }
}
