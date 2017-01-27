package com.example.nickgao.service;

/**
 * Created by nick.gao on 2014/8/28.
 */
public interface RestRequestListener {

    void onRequestSuccess();

    void onRequestFailure(int errorCode);
}
