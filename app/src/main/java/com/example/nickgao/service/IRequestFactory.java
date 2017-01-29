package com.example.nickgao.service;

import com.example.nickgao.service.model.contact.Contact;
import com.example.nickgao.service.request.RcRestRequest;
import com.example.nickgao.service.response.ClientInfoResponse;
import com.example.nickgao.service.response.RestPageResponse;


/**
 * Created by steve.chen on 7/1/14.
 */
public interface IRequestFactory {

    String TAG_EXTENSION_LIST = "[RC]ExtensionListRequest";
    String TAG_GETCLIENTINFO = "[RC]ClientInfo";
    String TAG_GET_SERVICE_NUMBER_RULE = "[RC]Service_number_rule";
    String TAG_GET_PHONE_NUMBER = "[RC]phone_number";
    String TAG_GET_API_VERSION = "[RC]api_version";
    String TAG_ACCOUNT = "[RC]account";
    String TAG_EXTENSIONINFO = "[RC]extensionInfo";
    String TAG_TIERTYPE = "[RC]TIERTYPE";
    String TAG_RINGOUT_CREATE = "[RC]ringout_create";
    String TAG_RINGOUT_STATUS = "[RC]ringout_status";
    String TAG_RINGOUT_CANCEL = "[RC]ringout_cancel";
    String TAG_RINGOUT_DIRECT = "[RC]ringout_direct";
    String TAG_GET_LANGUAGE_LIST = "[RC]language_list";
    String TAG_SYNC_LANGUAGE = "[RC]sync_language";
    String TAG_GET_PRESENCE = "[RC]presence";
    String TAG_UPDATE_EXTENSION = "[RC]update_extension";
    String TAG_HTTP_REGISTER = "[RC]httpRegister";
    String TAG_DIALING_PLANS = "[RC]DialingPlansRequest";
    String TAG_CONFRENCE_API = "[RC]ConferenceAPI";
    String TAG_SUBSCRIBE_API = "[RC]SubscribeAPI";
    String TAG_PERSONAL_CONTACTS_FSYNC = "[RC]PersonalContactsFSync";
    String TAG_PERSONAL_CONTACTS_ISYNC = "[RC]PersonalContactsISync";


    RcRestRequest<RestPageResponse<Contact>> createExtensionRequest(int pageSize);

    RcRestRequest<ClientInfoResponse> createClientInfoRequest();

}
