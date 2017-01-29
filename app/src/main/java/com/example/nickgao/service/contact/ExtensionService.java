package com.example.nickgao.service.contact;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.example.nickgao.contacts.adapters.contactsprovider.ContactMatcher;
import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.database.RCMProvider;
import com.example.nickgao.database.UriHelper;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.service.AbstractService;
import com.example.nickgao.service.IRequestFactory;
import com.example.nickgao.service.model.contact.Address;
import com.example.nickgao.service.model.contact.Contact;
import com.example.nickgao.service.model.contact.ContactInfo;
import com.example.nickgao.service.model.contact.Permission;
import com.example.nickgao.service.model.contact.PermissionsList;
import com.example.nickgao.service.model.extensioninfo.ProfileImage;
import com.example.nickgao.service.request.RcRestRequest;
import com.example.nickgao.service.request.RestPageRequest;
import com.example.nickgao.service.response.RestPageResponse;
import com.example.nickgao.utils.RCMConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

/**
 * Created by nick.gao on 1/27/17.
 */

public class ExtensionService extends AbstractService {

    private List<Contact> mContacts = new ArrayList<Contact>();

    public ExtensionService(IRequestFactory requestFactory) {
        super(requestFactory);
    }

    public void updateExtension(Context context) {
        doFreshRequest(context);
    }

    private void doFreshRequest(Context context) {
        doRequest(RCMConstants.PAGE_START_INDEX, context);
        mContacts.clear();
    }

    private void doRequest(int page, final Context context) {
        RcRestRequest<RestPageResponse<Contact>> request = this.mRequestFactory.createExtensionRequest(RCMConstants.PAGE_SIZE);
        request.registerOnRequestListener(new RcRestRequest.OnRequestListener<RestPageResponse<Contact>>() {
            @Override
            public void onSuccess(RcRestRequest<RestPageResponse<Contact>> request, RestPageResponse<Contact> response) {
                RestPageRequest<RestPageResponse<Contact>> pageRequest = (RestPageRequest<RestPageResponse<Contact>>) request;
                mContacts.addAll(Arrays.asList(response.getRecords()));
                if (pageRequest.hasMore()) {
                    pageRequest.createNextPageRequest().executeRequest(context);
                } else {
                    updateContact(mContacts, context);
                }

            }

            @Override
            public void onFail(RcRestRequest<RestPageResponse<Contact>> request, int errorCode) {
            }

            @Override
            public void onComplete(RcRestRequest<RestPageResponse<Contact>> request) {

            }
        });

        ((RestPageRequest<RestPageResponse<Contact>>) request).setPage(page);
        MktLog.i(TAG, "extension list service request");
        request.executeRequest(context);
    }


    public void updateContact(List<Contact> contacts, Context context) {
        long mailbox_id = CurrentUserSettings.getSettings(context).getCurrentMailboxId();

        HashMap<String, Contact> processed_new_ext_Map = new HashMap<String, Contact>();
        Collection<Contact> name_changed_ext_list = new ArrayList<Contact>();
        Collection<Contact> ext_removed_list = new ArrayList<Contact>();

        /* Combine two record with same extension mail box id into one record */
        for (Contact newExt : contacts) {
            String extMailBoxId = newExt.getId();
            processed_new_ext_Map.put(extMailBoxId, newExt);
        }

        /* Retrieve old extensions' list from the DB */
        Collection<Contact> old_ext = new ArrayList<Contact>();
        Collection<Contact> new_ext = processed_new_ext_Map.values();

        getOlderExtensionListFromDB(old_ext, context, mailbox_id);

        if (old_ext.size() > 0) {

            /* Remove obsolete entries from the DB */
            ContentResolver resolver = context.getContentResolver();
            StringBuilder where = new StringBuilder(" IN (");
            int count = 0;
            for (Contact ext : old_ext) {
                if (!new_ext.contains(ext)) {
                    if (processed_new_ext_Map.containsKey(ext.getId())) {
                        Contact contact = processed_new_ext_Map.get(ext.getId());
                        contact.setStarred(ext.isStarred());
                        contact.setSort(ext.getSort());
                        //Record the information is changed ext info
                        if (!TextUtils.equals(contact.getName(), ext.getName())) {
                            name_changed_ext_list.add(contact);
                        }
                        if (!TextUtils.equals(contact.getStatus(), ext.getStatus())) {
//                            status_changed_ext_list.add(contact);
                            //AB-13746 Not active extension is not shown in the company contact list for 6.5 account
                            if (!TextUtils.equals(RCMDataStore.ExtensionsTable.USER_STATUS_ENABLED, contact.getStatus())
                                    && !TextUtils.equals(RCMDataStore.ExtensionsTable.USER_STATUS_NOT_ACTIVATED, ext.getStatus())) {
                                ext_removed_list.add(ext);
                            }
                        }
                    } else {
                        ext_removed_list.add(ext);
                    }
                    where.append("'").append(ext.getId()).append("', ");
                } else {
//                    This part code just used to delete some dirty data, if upgrade from 6.4 to 6.5, this code is useless
                    if (!TextUtils.equals(RCMDataStore.ExtensionsTable.USER_STATUS_ENABLED, ext.getStatus())
                            && !TextUtils.equals(RCMDataStore.ExtensionsTable.USER_STATUS_NOT_ACTIVATED, ext.getStatus())) {
                        ext_removed_list.add(ext);
                        where.append("'").append(ext.getId()).append("', ");
                    }
                    // Remove already existing entries from the new extensions' list
                    new_ext.remove(ext);
                }
            }
            where.deleteCharAt(where.length() - 2);
            where.append(")");
            count += resolver.delete(UriHelper.getUri(RCMProvider.EXTENSIONS, mailbox_id),
                    RCMDataStore.ExtensionsTable.JEDI_MAILBOX_ID_EXT + where.toString(),
                    null);


        }

        if (!new_ext.isEmpty()) {
            insertNewExtensionToDB(new_ext, context, mailbox_id);
        }

        /* don't have to do these things
        //Update phone number table's corresponding extension name
        if (name_changed_ext_list.size() > 0) {
            ContactMatcher.syncExtensionName(RingCentralApp.getContextRC(), name_changed_ext_list);
        } else if (name_changed_ext_list.size() == 0 && old_ext.size() == 0) {
            ContactMatcher.synExtensionName(RingCentralApp.getContextRC());
        }*/

    }

    private void getOlderExtensionListFromDB(Collection<Contact> old_ext, Context context, long mailbox_id) {
        Cursor c = context.getContentResolver().query(
                UriHelper.getUri(RCMProvider.EXTENSIONS, mailbox_id), null, null, null, null);

        if (c != null && c.getCount() > 0) {

            Contact extInfo;
            c.moveToFirst();
            do {
                extInfo = new Contact();
                extInfo.setId(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.JEDI_MAILBOX_ID_EXT)));
                extInfo.setExtensionNumber(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.JEDI_PIN)));
                extInfo.setStatus(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.REST_STATUS)));
                extInfo.setName(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.RCM_DISPLAY_NAME)));
                extInfo.setType(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.REST_TYPE)));
                ContactInfo contactInfo = new ContactInfo();
                contactInfo.setFirstName(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.JEDI_FIRST_NAME)));
                contactInfo.setLastName(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.JEDI_LAST_NAME)));
                contactInfo.setEmail(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.JEDI_EMAIL)));
                contactInfo.setBusinessPhone(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.JEDI_CONTACT_PHONE)));
                Address address = new Address();
                address.setStreet(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.JEDI_ADDRESS_LINE_1)));
                address.setCity(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.JEDI_CITY)));
                address.setCountry(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.JEDI_COUNTRY)));
                address.setState(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.JEDI_STATE)));
                address.setZip(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.JEDI_ZIPCODE)));
                contactInfo.setBusinessAddress(address);
                extInfo.setContact(contactInfo);
                PermissionsList permissionsList = new PermissionsList();
                Permission admin = new Permission();
                admin.setEnabled(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.REST_ADMIN_PERMISSION)));
                Permission internationalCalling = new Permission();
                internationalCalling.setEnabled(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.REST_INTERNATIONALCALLING_PERMISSION)));
                permissionsList.setAdmin(admin);
                permissionsList.setInternationalCalling(internationalCalling);
                extInfo.setPermissions(permissionsList);
                //AB-13850 The company favorite record is removed from list after go to server web to change favorite company record's name or extension number.
                Integer starred = c.getInt(c.getColumnIndex(RCMDataStore.ExtensionsTable.RCM_STARRED));
                extInfo.setStarred(starred != null && starred != 0);
                extInfo.setSort(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.RCM_SORT)));

                ProfileImage profileImage = new ProfileImage();
                profileImage.setUri(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.PROFILE_IMAGE)));
                profileImage.setEtag(c.getString(c.getColumnIndex(RCMDataStore.ExtensionsTable.PROFILE_IMAGE_ETAG)));

                //fixed me
              //  ProfileImageOperator.setProfileUriArray(c, profileImage);

                extInfo.setProfileImage(profileImage);

                old_ext.add(extInfo);
            } while (c.moveToNext());
        } else {

        }

        if (c != null && !c.isClosed()) {
            c.close();
        }
    }


    private void insertNewExtensionToDB(Collection<Contact> new_ext, Context context, long mailbox_id) {
        /* Store new extensions list to the database */
        ContentValues item;
        Vector<ContentValues> values = new Vector<ContentValues>();

        for (Contact ext_info : new_ext) {
            if (!TextUtils.equals(RCMDataStore.ExtensionsTable.USER_STATUS_ENABLED, ext_info.getStatus())
                    && !TextUtils.equals(RCMDataStore.ExtensionsTable.USER_STATUS_NOT_ACTIVATED, ext_info.getStatus())) {
                continue;
            }
            item = new ContentValues();
            item.put(RCMDataStore.ExtensionsTable.MAILBOX_ID, mailbox_id);
            item.put(RCMDataStore.ExtensionsTable.RCM_DISPLAY_NAME, ext_info.getName());
            item.put(RCMDataStore.ExtensionsTable.JEDI_MAILBOX_ID_EXT, ext_info.getId());
            item.put(RCMDataStore.ExtensionsTable.JEDI_PIN, ext_info.getExtensionNumber());
            if (ext_info.getContact() != null) {
                item.put(RCMDataStore.ExtensionsTable.JEDI_FIRST_NAME, ext_info.getContact().getFirstName());
                item.put(RCMDataStore.ExtensionsTable.JEDI_LAST_NAME, ext_info.getContact().getLastName());
                item.put(RCMDataStore.ExtensionsTable.JEDI_EMAIL, ext_info.getContact().getEmail());
                item.put(RCMDataStore.ExtensionsTable.JEDI_CONTACT_PHONE, ext_info.getContact().getBusinessPhone());
                if (ext_info.getContact().getBusinessAddress() != null) {
                    item.put(RCMDataStore.ExtensionsTable.JEDI_ADDRESS_LINE_1, ext_info.getContact().getBusinessAddress().getStreet());
                    item.put(RCMDataStore.ExtensionsTable.JEDI_CITY, ext_info.getContact().getBusinessAddress().getCity());
                    item.put(RCMDataStore.ExtensionsTable.JEDI_COUNTRY, ext_info.getContact().getBusinessAddress().getCountry());
                    item.put(RCMDataStore.ExtensionsTable.JEDI_STATE, ext_info.getContact().getBusinessAddress().getState());
                    item.put(RCMDataStore.ExtensionsTable.JEDI_ZIPCODE, ext_info.getContact().getBusinessAddress().getZip());
                }
            }
            item.put(RCMDataStore.ExtensionsTable.RCM_STARRED, ext_info.isStarred() ? 1 : 0);
            item.put(RCMDataStore.ExtensionsTable.RCM_SORT, ext_info.getSort());
            item.put(RCMDataStore.ExtensionsTable.REST_STATUS, ext_info.getStatus());
            item.put(RCMDataStore.ExtensionsTable.REST_TYPE, ext_info.getType());
            if (ext_info.getPermissions() != null) {
                //AB-13806 [6.3 accout]Company contact can't sync automatically after upgrade
                item.put(RCMDataStore.ExtensionsTable.REST_ADMIN_PERMISSION, ext_info.getPermissions().getAdmin() != null ? ext_info.getPermissions().getAdmin().getEnabled() : null);
                item.put(RCMDataStore.ExtensionsTable.REST_INTERNATIONALCALLING_PERMISSION, ext_info.getPermissions().getInternationalCalling() != null ? ext_info.getPermissions().getInternationalCalling().getEnabled() : null);
            }

            if(ext_info.getProfileImage() != null) {
                item.put(RCMDataStore.ExtensionsTable.PROFILE_IMAGE,ext_info.getProfileImage().getUri());
                item.put(RCMDataStore.ExtensionsTable.PROFILE_IMAGE_ETAG,ext_info.getProfileImage().getEtag());

                if(ext_info.getProfileImage().getScales() != null) {
                    item.put(RCMDataStore.ExtensionsTable.PROFILE_IMAGE_SMALL_URI,ext_info.getProfileImage().getScales()[0].getUri());
                    item.put(RCMDataStore.ExtensionsTable.PROFILE_IMAGE_MEDIUM_URI,ext_info.getProfileImage().getScales()[1].getUri());
                    item.put(RCMDataStore.ExtensionsTable.PROFILE_IMAGE_LARGE_URI,ext_info.getProfileImage().getScales()[2].getUri());
                }
            }

            values.addElement(item);
        }

        if (values.size() > 0) {

            context.getContentResolver().bulkInsert(
                    UriHelper.getUri(RCMProvider.EXTENSIONS), values.toArray(new ContentValues[values.size()]));
        }
    }
}
