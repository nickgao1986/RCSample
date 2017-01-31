package com.example.nickgao.service;

import com.example.nickgao.R;
import com.example.nickgao.contacts.CloudFavoriteContactInfo;
import com.example.nickgao.contacts.CreateSingleContactRequest;
import com.example.nickgao.contacts.DeleteContactRequest;
import com.example.nickgao.contacts.UpdateCloudFavoriteRequest;
import com.example.nickgao.contacts.UpdateContactsRequest;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContactInfo;
import com.example.nickgao.network.RestRequest;
import com.example.nickgao.service.model.contact.Contact;
import com.example.nickgao.service.request.RcRestRequest;
import com.example.nickgao.service.request.RestListRequest;
import com.example.nickgao.service.request.RestPageRequest;
import com.example.nickgao.service.response.ClientInfoResponse;
import com.example.nickgao.service.response.RestListResponse;
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

    @Override
    public RcRestRequest<CloudPersonalContactInfo> createSingleContactRequest(String requestBody) {
        Type type = new TypeToken<CloudPersonalContactInfo>() {
        }.getType();
        return new CreateSingleContactRequest<>(R.string.rest_create_single_contact, type, RestRequest.HttpMethod.POST, TAG_CREATE_SINGLE_CONTACT, requestBody);
    }

    @Override
    public RcRestRequest<CloudPersonalContactInfo> updateContactRequest(String requestBody) {
        Type type = new TypeToken<CloudPersonalContactInfo>() {
        }.getType();
        return new UpdateContactsRequest<>(R.string.rest_update_delete_single_contact, type, RestRequest.HttpMethod.PUT, TAG_UPDATE_CONTACT, requestBody);
    }

    @Override
    public RcRestRequest<CloudPersonalContactInfo> deleteContactRequest() {
        Type type = new TypeToken<CloudPersonalContactInfo>() {
        }.getType();
        return new DeleteContactRequest<>(R.string.rest_update_delete_single_contact, type, RestRequest.HttpMethod.DELETE, TAG_DELETE_CONTACT);
    }

    @Override
    public RcRestRequest<RestListResponse<CloudFavoriteContactInfo>> getCloudFavoriteList() {
        Type type = new TypeToken<RestListResponse<CloudFavoriteContactInfo>>() {
        }.getType();
        return new RestListRequest<>(R.string.rest_path_cloud_favorite, type, RestRequest.HttpMethod.GET, TAG_GET_CLOUD_FAVORITE);
    }

    @Override
    public RcRestRequest<RestListResponse<CloudFavoriteContactInfo>> updateCloudFavoriteList(String requestBody) {
        Type type = new TypeToken<RestListResponse<CloudFavoriteContactInfo>>() {
        }.getType();
        return new UpdateCloudFavoriteRequest<>(R.string.rest_path_cloud_favorite, type, RestRequest.HttpMethod.PUT, TAG_UPDATE_CLOUD_FAVORITE, requestBody);
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
