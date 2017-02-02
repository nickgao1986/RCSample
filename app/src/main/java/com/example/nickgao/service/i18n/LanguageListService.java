package com.example.nickgao.service.i18n;

import com.example.nickgao.datastore.LanguageDataStore;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.service.AbstractService;
import com.example.nickgao.service.IRequestFactory;
import com.example.nickgao.service.model.i18n.LanguageRecord;
import com.example.nickgao.service.request.RcRestRequest;
import com.example.nickgao.service.request.RestPageRequest;
import com.example.nickgao.service.response.RestPageResponse;
import com.example.nickgao.utils.RCMConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nick.gao on 2/1/17.
 */

public class LanguageListService extends AbstractService {
    public static final String LOG_TAG = "[RC]LanguageListService";
    private List<LanguageRecord> mLanguageRecords = new ArrayList<LanguageRecord>();

    public LanguageListService(IRequestFactory requestFactory) {
        super(requestFactory);
    }

    public void updateData() {
        doFreshRequest();
    }

    private void doFreshRequest() {
        mLanguageRecords.clear();
        doRequest(RCMConstants.PAGE_START_INDEX);
    }

    private void doRequest(int page) {

        RcRestRequest<RestPageResponse<LanguageRecord>> request = this.mRequestFactory.createLanguageListRequest(RCMConstants.PAGE_SIZE);
        request.registerOnRequestListener(new RcRestRequest.OnRequestListener<RestPageResponse<LanguageRecord>>() {
            @Override
            public void onSuccess(RcRestRequest<RestPageResponse<LanguageRecord>> request, RestPageResponse<LanguageRecord> response) {
                RestPageRequest<RestPageResponse<LanguageRecord>> pageRequest = (RestPageRequest<RestPageResponse<LanguageRecord>>) request;
                mLanguageRecords.addAll(Arrays.asList(response.getRecords()));
                MktLog.i(TAG,"====records="+response.getRecords()[0].getLocaleCode());
                boolean pagingMode = pageRequest.hasMore();
                if (pageRequest.hasMore()) {
                    pageRequest.createNextPageRequest().executeRequest(RingCentralApp.getContextRC());
                } else {
                    try {
                        LanguageDataStore.processPage(mLanguageRecords);
                    } catch (IOException ex) {
                        MktLog.w(LOG_TAG, "message ex=" + ex);
                    }
                    if (mListener != null) {
                        mListener.onRequestSuccess();
                    }
                }
            }

            @Override
            public void onFail(RcRestRequest<RestPageResponse<LanguageRecord>> request, int errorCode) {
                if (mListener != null) {
                    mListener.onRequestFailure(errorCode);
                }
            }

            @Override
            public void onComplete(RcRestRequest<RestPageResponse<LanguageRecord>> request) {
            }
        });

        ((RestPageRequest<RestPageResponse<LanguageRecord>>) request).setPage(page);
        MktLog.i(TAG, "language list service request");
        request.executeRequest(RingCentralApp.getContextRC());
    }

}
