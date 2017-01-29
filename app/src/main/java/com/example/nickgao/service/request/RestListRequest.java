package com.example.nickgao.service.request;

import java.lang.reflect.Type;

/**
 * Created by nick.gao on 2014/7/16.
 */
public class RestListRequest<T> extends RcRestRequest<T> {

    public RestListRequest(int requestId, Type responseType, HttpMethod method, String logTag) {
        super(requestId, responseType, method, logTag);
    }
}
