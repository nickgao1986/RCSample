package com.example.nickgao.service.response;

/**
 * Created by steve.chen on 6/30/14.
 */
public class RestListResponse<T> extends AbstractResponse {

    private T[] records;

    public T[] getRecords() {
        return records;
    }

    public void setRecords(T[] records) {
        this.records = records;
    }

}
