package com.example.nickgao.contacts;

import android.content.Context;

import com.example.nickgao.R;
import com.example.nickgao.service.request.RcRestRequest;
import com.example.nickgao.utils.RCMConstants;

import org.apache.commons.lang.StringUtils;

/**
 * Created by nick.gao on 1/30/17.
 */

public class CloudPersonalContactsSyncRequest extends RcRestRequest<CloudPersonalContactsSyncResponse> {

    private static final String SYNC_TYPE_ISYNC = "ISync";
    private static final String TOKEN_FORMAT = "&syncToken=%1$s";

    private int mPage = RCMConstants.PAGE_START_INDEX;

    private int mPageSize;

    private String mSyncToken;

    public CloudPersonalContactsSyncRequest(int requestId, int pageSize, String syncToken, String logTag) {
        super(requestId, CloudPersonalContactsSyncResponse.class, HttpMethod.GET, logTag);
        mPageSize = pageSize;
        mSyncToken = syncToken;
    }

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        this.mPage = page;
    }

    public int getPageSize() {
        return mPageSize;
    }

    @Override
    protected void initRequestPath(Context context, Object... args) {

        super.initRequestPath(context, args);

        if (!mRequestPath.contains("?")) {
            mRequestPath += "?";
        } else {
            mRequestPath += "&";
        }
        String pagePath = context.getString(R.string.page_template, getPage(), getPageSize());
        mRequestPath += pagePath;
        if (StringUtils.isNotEmpty(mSyncToken)) {
            String tokenPath = String.format(TOKEN_FORMAT, mSyncToken);
            mRequestPath += tokenPath;
        }
    }

    public String getNextPageUri() {
        if (hasMore()) {
            return mResponseData.getNextPageUri();
        }

        return null;
    }

    public boolean hasMore() {
        return mResponseData != null && StringUtils.isNotEmpty(mResponseData.getNextPageUri());
    }

    public CloudPersonalContactsSyncRequest createNextPageRequest() {
        if (hasMore()) {
            String nextPageUri = this.getNextPageUri();
            final String syncToken = mResponseData.getSyncInfo().syncToken;
            final String syncType = mResponseData.getSyncInfo().syncType;
            if (SYNC_TYPE_ISYNC.equalsIgnoreCase(syncType)) {
                if (StringUtils.isNotEmpty(syncToken)) {
                    String tokenPath = String.format(TOKEN_FORMAT, syncToken);
                    nextPageUri += tokenPath;
                }
            }

            CloudPersonalContactsSyncRequest request = new CloudPersonalContactsSyncRequest(mRequestId, mPageSize, null, mLogTag);
            request.setRequestUri(nextPageUri);
            request.registerOnRequestListener(mOnRequestListener);
            return request;
        }

        return null;
    }
}
