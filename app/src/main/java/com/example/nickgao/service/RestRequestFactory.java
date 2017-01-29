package com.example.nickgao.service;

import com.example.nickgao.R;
import com.example.nickgao.network.RestRequest;
import com.example.nickgao.service.model.contact.Contact;
import com.example.nickgao.service.request.RcRestRequest;
import com.example.nickgao.service.request.RestListRequest;
import com.example.nickgao.service.request.RestPageRequest;
import com.example.nickgao.service.response.ClientInfoResponse;
import com.example.nickgao.service.response.RestPageResponse;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

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


    @Override
    public RcRestRequest<RestPageResponse<Contact>> createExtensionRequest(int pageSize) {

        Type type = new TypeToken<RestPageResponse<Contact>>() {
        }.getType();
        return new RestPageRequest<RestPageResponse<Contact>>(R.string.rest_path_extension_list, pageSize, type, RestRequest.HttpMethod.GET, TAG_EXTENSION_LIST);
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
