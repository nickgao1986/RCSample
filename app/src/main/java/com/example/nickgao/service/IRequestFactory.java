package com.example.nickgao.service;

import com.example.nickgao.contacts.CloudFavoriteContactInfo;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContactInfo;
import com.example.nickgao.service.model.contact.Contact;
import com.example.nickgao.service.model.extensioninfo.ExtensionInfoResponse;
import com.example.nickgao.service.model.i18n.LanguageRecord;
import com.example.nickgao.service.request.RcRestRequest;
import com.example.nickgao.service.response.ClientInfoResponse;
import com.example.nickgao.service.response.RestListResponse;
import com.example.nickgao.service.response.RestPageResponse;


/**
 * Created by steve.chen on 7/1/14.
 */
public interface IRequestFactory {

    String TAG_EXTENSION_LIST = "[RC]ExtensionListRequest";
    String TAG_GETCLIENTINFO = "[RC]ClientInfo";
    String TAG_GETPERMISSIONS = "[RC]Permissions";
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
    String TAG_UNSUBSCRIBE_API = "[RC]UnSubscribeAPI";
    String TAG_RENEW_SUBSCRIBE_API = "[RC]ReNewSubscribeAPI";
    String TAG_PERSONAL_CONTACTS_FSYNC = "[RC]PersonalContactsFSync";
    String TAG_PERSONAL_CONTACTS_ISYNC = "[RC]PersonalContactsISync";
    String TAG_DOWNLOAD_PHONE_DATA = "[RC]DownloadPhoneData";
    String TAG_CREATE_SINGLE_CONTACT = "[RC]CreateSingleContact";
    String TAG_UPDATE_CONTACT = "[RC]UpdateContact";
    String TAG_DELETE_CONTACT = "[RC]DeleteContact";
    String TAG_GET_CLOUD_FAVORITE = "[RC]GetCloudFavorite";
    String TAG_UPDATE_CLOUD_FAVORITE = "[RC]UpdateCloudFavorite";
    String TAG_GET_BLF_LIST = "[RC]GetBLFList";
    String TAG_GET_BLF_PRESENCE = "[RC]GetBLFListPresence";


    RcRestRequest<RestPageResponse<Contact>> createExtensionRequest(int pageSize);

    RcRestRequest<ClientInfoResponse> createClientInfoRequest();

    RcRestRequest<CloudPersonalContactInfo> createSingleContactRequest(String requestBody);

    RcRestRequest<CloudPersonalContactInfo> updateContactRequest(String requestBody);

    RcRestRequest<CloudPersonalContactInfo> deleteContactRequest();

    RcRestRequest<RestListResponse<CloudFavoriteContactInfo>> getCloudFavoriteList();

    RcRestRequest<RestListResponse<CloudFavoriteContactInfo>> updateCloudFavoriteList(String requestBody);

    RcRestRequest<RestPageResponse<LanguageRecord>> createLanguageListRequest(int pageSize);

    RcRestRequest<ExtensionInfoResponse> createGetExtensionInfoRequest();


}
