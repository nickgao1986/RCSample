package com.example.nickgao.service;

import com.example.nickgao.androidsample11.R;
import com.example.nickgao.network.RestRequest;
import com.example.nickgao.service.response.ClientInfoResponse;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

/**
 * Created by steve.chen on 6/30/14.
 */
public class RestRequestFactory implements IRequestFactory {


    @Override
    public RcRestRequest<ClientInfoResponse> createClientInfoRequest() {
        Type type = new TypeToken<ClientInfoResponse>() {
        }.getType();
        return new RestListRequest<ClientInfoResponse>(R.string.rest_path_get_client_info, type, RestRequest.HttpMethod.GET, TAG_GETCLIENTINFO);
    }



    static class DeliverMode {
        public String  transportType;
        public String  registrationId;
        public String  certificateName;
        public boolean encryption;
    }

    static class SubscribeMode {
        public DeliverMode deliveryMode;

        public String[] eventFilters;
    }

}
