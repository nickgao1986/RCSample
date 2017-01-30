package com.example.nickgao.contacts;

import android.content.Context;

import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContactInfo;
import com.example.nickgao.service.IRequestFactory;
import com.example.nickgao.service.request.RcRestRequest;

import java.util.List;

/**
 * Created by nick.gao on 1/30/17.
 */

public class CloudPersonalContactsServiceFSync extends CloudPersonalContactsServiceAbstract {

    public CloudPersonalContactsServiceFSync(IRequestFactory requestFactory) {
        super(requestFactory);
    }

    @Override
    protected CloudPersonalContactsSyncRequest createRequest() {
      //  return mRequestFactory.createPersonalContactsFSync(RCMConstants.PAGE_SIZE);
        return null;
    }

    @Override
    protected void processResult(Context context, List<CloudPersonalContactInfo> contacts) {
//        MktLog.d(TAG, "Contact FSync save begin " + contacts.size());
//        if(contacts == null || contacts.isEmpty()) {
//            return;
//        }
//
//        try {
//            long time = System.currentTimeMillis();
//            //delete old data
//            CloudPersonalContactLoader.deleteAllContactInDB();
//
//            //translate to contact
//            List<Contact> contactList = new ArrayList<>();
//            ContactsLoader.ContactPhoneParser phoneParser = new ContactsLoader.ContactPhoneParser();
//            for (CloudPersonalContactInfo personalContact : contacts) {
//                CloudPersonalContact cpContact = ContactsUtils.translateToCloudPersonalContact(CloudPersonalContact.CloudContactType.SERVER, personalContact, phoneParser);
//                contactList.add(cpContact);
//            }
//            contacts.clear();
//            //contacts save into database
//            Map<Long, Contact> addedContacts = CloudPersonalContactLoader.addContactsToDB(contactList, RCMDataStore.CloudContactSyncStatus.Synced);
//            ContactsProvider.getInstance().bulkInsert(Contact.ContactType.CLOUD_PERSONAL, addedContacts, true);
//            //PersonalFavorites.onCloudPersonalContactFullSync(addedContacts);
//            MktLog.d(TAG, "Contact FSync translation: spent=" + (System.currentTimeMillis() - time));
//        } catch (Exception e) {
//            if (LogSettings.ENGINEERING) {
//                MktLog.e(TAG, "ISync cloud contacts in database, " + e.getMessage());
//            }
//            saveToken("");
//        }
    }

    @Override
    protected void onFailed(RcRestRequest<CloudPersonalContactsSyncResponse> request, int errorCode) {
        saveToken("");
    }


}
