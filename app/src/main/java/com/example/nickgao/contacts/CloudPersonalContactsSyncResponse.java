package com.example.nickgao.contacts;

import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContactInfo;
import com.example.nickgao.network.RestSyncInfo;
import com.example.nickgao.service.response.RestListResponse;

/**
 * Created by nick.gao on 1/30/17.
 */

public class CloudPersonalContactsSyncResponse extends RestListResponse<CloudPersonalContactInfo> {
    RestSyncInfo syncInfo;
    long nextPageId;
    String nextPageUri;

    public RestSyncInfo getSyncInfo() {
        return  syncInfo;
    }

    public void setSyncInfo(RestSyncInfo syncInfo) {
        this.syncInfo = syncInfo;
    }

    public long getNextPageId() {
        return nextPageId;
    }

    public  void setNextPageId(long nextPageId) {
        this.nextPageId = nextPageId;
    }

    public String getNextPageUri() {
        return nextPageUri;
    }

    public void setNextPageUri(String nextPageUri) {
        this.nextPageUri = nextPageUri;
    }
}
