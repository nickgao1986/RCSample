package com.example.nickgao.service.request;

import android.content.Context;
import android.text.TextUtils;

import com.example.nickgao.R;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.service.response.RestPageResponse;
import com.example.nickgao.utils.RCMConstants;

import java.lang.reflect.Type;

/**
 * Created by steve.chen on 7/7/14.
 */

public class RestPageRequest<T> extends RcRestRequest<T> {

    private int mPage = RCMConstants.PAGE_START_INDEX;

    private int mPageSize;
    private static final String TAG = "RestPageRequest";

    public RestPageRequest(int requestId, int pageSize, Type responseType, HttpMethod method, String logTag) {
        super(requestId, responseType, method, logTag);
        mPageSize = pageSize;
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

    public void setPageSize(int pageSize) {
        this.mPageSize = pageSize;
    }

    @Override
    protected void initRequestPath(Context context, Object... args) {

        super.initRequestPath(context, args);

        if (!mRequestPath.contains("?")) {
            mRequestPath += "?";
        } else {
            mRequestPath += "&";
        }
        MktLog.i(TAG,"====page="+mPage+"pageSize="+mPageSize);
        String pagePath = context.getString(R.string.page_template, getPage(), getPageSize());
        mRequestPath += pagePath;
    }

    public String getNextPageUri() {
        String nextPageUri = null;

        if (hasMore()) {
            RestPageResponse<T> restPageResponse = (RestPageResponse<T>) this.mResponseData;
            nextPageUri = restPageResponse.getNavigation().getNextPage().getUri();
        }

        return nextPageUri;
    }

    public boolean hasMore() {
        RestPageResponse<T> restPageResponse = (RestPageResponse<T>) this.mResponseData;
        return restPageResponse != null && restPageResponse.getNavigation() != null && restPageResponse.getNavigation().getNextPage() != null && !TextUtils.isEmpty(restPageResponse.getNavigation().getNextPage().getUri());
    }

    public RestPageRequest<T> createNextPageRequest() {
        RestPageRequest<T> request = null;
        if (hasMore()) {
            request = new RestPageRequest<T>(mRequestId, mPageSize, mResponseType, mMethod, mLogTag);
            request.setRequestUri(this.getNextPageUri());
            request.registerOnRequestListener(mOnRequestListener);
        }

        return request;
    }

}
