package com.example.nickgao.androidsample11;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Contacts.ContactMethods;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;
import android.provider.Contacts.Photos;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContact;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContactInfo;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsProvider;
import com.example.nickgao.contacts.adapters.contactsprovider.DeviceContact;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.service.model.contact.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

public class ContactsUtils {

	private static String buildVersion = null;
	private static boolean isInitialized = false;
	
	/** String value. In SDK 3 it = People._ID;*/
	public static String People_ID					= People._ID;//"photo_data";
	/** String value. In SDK 3 it = People.DEFAULT_SORT_ORDER*/
	public static String People_SortOrder			= People.DEFAULT_SORT_ORDER;
	/** String value. In SDK 3 it = People.DISPLAY_NAME*/
	public static String People_DisplayName			= People.DISPLAY_NAME;
	/** String value. In SDK 3 it = People.PRIMARY_PHONE_ID*/
	public static String People_Has_Phone_Number	= People.PRIMARY_PHONE_ID;
	/** String value. In SDK 3 it = People._ID;*/
	public static String People_PhotoID				= People._ID;
	
	/** String value. In SDK 3 it = Phones._ID*/
	public static String Phone_ID					= Phones._ID;
	/** String value. In SDK 3 it = Phones.NUMBER*/
	public static String Phone_Number 				= Phones.NUMBER;
	/** String value. In SDK 3 it = Phones.TYPE*/
	public static String Phone_Type 				= Phones.TYPE;
	/** String value. In SDK 3 it = Phones.ISPRIMARY*/
	public static String Phone_IsPrimary 			= Phones.ISPRIMARY;
	/** String value. In SDK 3 it = Phones.PERSON_ID*/
	public static String Phone_PersonID 			= Phones.PERSON_ID;
	/** String value. In SDK 3 it = Photos.DATA*/
	public static String Photo_Photo				= Photos.DATA;
	
	/** String value. In SDK 3 it = ContactMethods._ID*/
	public static String Email_Person_ID			= ContactMethods.PERSON_ID;
	/** String value. In SDK 3 it = ContactMethods._ID*/
	public static String Email_Data					= ContactMethods.DATA;
	/** String value. In SDK 3 it = ContactMethods._ID*/
	public static String Email_Type					= ContactMethods.TYPE;
	/** String value. In SDK 3 it = Phones.PERSON_ID*/
	public static String PhoneLookup_ID				= Phones.PERSON_ID;
	/** String value. In SDK 3 it = Not supported in SDK3. people ID instead of photo id*/
	public static String PhoneLookup_Photo_ID		= Phones.PERSON_ID; 
	
	// --- URI --------------
	/** URI value. In SDK 3 it = People.CONTENT_URI*/
	public static Uri Uri_People 					= People.CONTENT_URI;
	/** URI value. In SDK 3 it = People.CONTENT_FILTER_URI*/
	public static Uri Uri_People_Filter				= People.CONTENT_FILTER_URI;
	/** URI value. In SDK 3 it = Phones.CONTENT_URI*/
	public static Uri Uri_Phone 					= Phones.CONTENT_URI;
	/** URI value. In SDK 3 it = Phones.CONTENT_URI*/
	public static Uri Uri_Phone_lookup 				= Phones.CONTENT_FILTER_URL;
	/** URI value. In SDK 3 it = Photos.CONTENT_URI*/
	public static Uri Uri_Photo 					= Photos.CONTENT_URI;	
	/** URI value. In SDK 3 it = ContactMethods.CONTENT_EMAIL_URI*/
	public static Uri Uri_Email 					= ContactMethods.CONTENT_EMAIL_URI;

	public static final String EMAIL_REGEX = "^[a-z0-9!#$%&'*+=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
	public static final String PHONE_NUMBER_FILTER = "+0123456789";


	@SuppressWarnings("static-access")
	public static String getBuildVersion(){
		if (buildVersion == null){
			buildVersion = new Build.VERSION().SDK;
		}
		return buildVersion;
	}
	
	/**
     * Initialize all Contacts variables
     */
	static {
	    if (!isInitialized){
    	    	// People
    	    	People_DisplayName 		= "display_name"; 
    	    	People_SortOrder 		= People_DisplayName  + " COLLATE LOCALIZED ASC";
    	    	People_Has_Phone_Number	= "has_phone_number";
    	    	People_PhotoID			= "photo_id";

    	    	// Phones
    	    	Phone_Number 			= "data1"; 		
    	    	Phone_Type 				= "data2";
        		Phone_IsPrimary 		= "is_super_primary"; 											// DATA.IS_SUPER_PRIMARY = Phone.IS_SUPER_PRIMARY = "is_super_primary"
        		Phone_PersonID 			= "raw_contact_id";
    
     			//Email
     			Email_Person_ID			= "raw_contact_id";
     			Email_Data				= "data1";
     			Email_Type				= "data2";
     			
     			//PhoneLookup
     			PhoneLookup_ID			= "_id";
     			PhoneLookup_Photo_ID	= "photo_id";
     			
 			
        		// Uri
     			Uri_People 				= Uri.parse("content://com.android.contacts/contacts");	
     			Uri_People_Filter		= Uri.parse("content://com.android.contacts/contacts/filter");																						// ContactContracts.Data 	| Data.CONTENT_URI = content://com.android.contacts/data
     			Uri_Phone 				= Uri.parse("content://com.android.contacts/data/phones");		//							| Phone.CONTENT_URI =content://com.android.contacts/data/phones
     			Uri_Phone_lookup 		= Uri.parse("content://com.android.contacts/phone_lookup");	// ContactsContract.PhoneLookup.CONTENT_FILTER_URI = content://com.android.contacts/phone_lookup
    
     			Uri_Photo 				= Uri.parse("content://com.android.contacts/data");				//
     			Uri_Email 				= Uri.parse("content://com.android.contacts/data");				// 
            }
	        isInitialized = true;
	}

	
	public static Bitmap getContactPhoto(Context context, long photoId, BitmapFactory.Options options) {
        if (photoId < 0) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId),
                    new String[] { Photo.PHOTO }, null, null, null);

            if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                byte[] photoData = cursor.getBlob(0);
                // Workaround for Android Issue 8488 http://code.google.com/p/android/issues/detail?id=8488
                if (options == null) {
                    options = new BitmapFactory.Options();
                }
                options.inTempStorage = new byte[16 * 1024];
                options.inSampleSize = 2;
                return BitmapFactory.decodeByteArray(photoData, 0, photoData.length, options);
            }
        } catch (Throwable error) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

	public static String phoneNumberNormalize(String source, int start, int end) {
		StringBuilder sb = new StringBuilder();
		char c;
		for (int i = start; i < end; i++) {
			c = source.charAt(i);
			if (PHONE_NUMBER_FILTER.indexOf(c) != -1) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static List<Contact.TypeAddress> orderedAddress(List<Contact.TypeAddress> addressList) {
		List<Contact.TypeAddress> orderedAddress = new ArrayList<>();
		AddressArray homeAddress = new AddressArray(CloudPersonalContact.HOME_ADDRESS_SIZE);
		AddressArray workAddress = new AddressArray(CloudPersonalContact.WORK_ADDRESS_SIZE);
		AddressArray otherAddress = new AddressArray(CloudPersonalContact.OTHER_ADDRESS_SIZE);
		for(Contact.TypeAddress address : addressList) {
			if(address.getType() == CloudPersonalContact.AddressType.HOME_ADDRESS.ordinal()) {
				homeAddress.addData(address);
			}else if(address.getType() == CloudPersonalContact.AddressType.BUSINESS_ADDRESS.ordinal()) {
				workAddress.addData(address);
			}else if(address.getType() == CloudPersonalContact.AddressType.OTHER_ADDRESS.ordinal()){
				otherAddress.addData(address);
			}
		}
		homeAddress.output(orderedAddress);
		workAddress.output(orderedAddress);
		otherAddress.output(orderedAddress);
		return orderedAddress;
	}


	public static class StringArray extends Contact.DataArray<String> {
		public StringArray(int maxSize) {
			super(maxSize);
		}
	}

	public static class PhoneArray extends Contact.DataArray<Contact.TypeValue> {
		public PhoneArray(int maxSize) {
			super(maxSize);
		}
	}

	public static class AddressArray extends Contact.DataArray<Contact.TypeAddress> {
		public AddressArray(int maxSize) {
			super(maxSize);
		}
	}

	public static String getValidZipCode(String source) {
		StringBuilder sb = new StringBuilder();
		if (!TextUtils.isEmpty(source)) {
			char c;
			for (int i = 0; i < source.length(); i++) {
				c = source.charAt(i);
				if (isDigitOrLetter(c)) {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	public static boolean isDigitOrLetter(char c) {
		return (c >= 'a' && c <= 'z') ||
				(c >= 'A' && c <= 'Z') ||
				(c >= '0' && c <= '9');
	}

	public static boolean validateEmail(String value) {
		if(TextUtils.isEmpty(value)) {
			return false;
		}
		Pattern pattern = Pattern.compile(EMAIL_REGEX);
		return pattern.matcher(value.toLowerCase()).matches();
	}

	public static boolean validate(String firstName, String lastName, String company, List<Contact.TypeValue> phones) {
		return !Contact.isEmpty(firstName) || !Contact.isEmpty(lastName) || !Contact.isEmpty(company) || !phones.isEmpty();
	}

	public static boolean validate(String firstName, String lastName, String nickName, String company, List<Contact.TypeValue> phones) {
		return !Contact.isEmpty(firstName) || !Contact.isEmpty(lastName) || !Contact.isEmpty(nickName) || !Contact.isEmpty(company) || !phones.isEmpty();
	}


	/**
	 * Translate CloudPersonalContact object to cloud single info
	 * @param contact
	 * @return
	 */
	public static CloudPersonalContactInfo translateToCloudPersonalContactInfo(CloudPersonalContact contact) {
		CloudPersonalContactInfo cloudInfo = new CloudPersonalContactInfo();
		try {
			cloudInfo.id = contact.getId();
			cloudInfo.uri = contact.getUri();
			cloudInfo.availability = contact.getAvailability();
			cloudInfo.firstName = contact.getFirstName();
			cloudInfo.lastName = contact.getLastName();
			cloudInfo.middleName = contact.getMiddleName();
			cloudInfo.nickName = contact.getNickName();
			cloudInfo.company = contact.getCompany();
			cloudInfo.jobTitle = contact.getJobTitle();
			cloudInfo.birthday = contact.getBirthday();
			cloudInfo.webPage = contact.getWebPage(0);
			cloudInfo.notes = contact.getNotes();
			//translate phones
			List<Contact.TypeValue> phones = contact.getE164PhoneNumbers();
			CloudPersonalContact.PhoneType[] phoneTypes = CloudPersonalContact.PhoneType.values();
			String phoneValue;
			for (Contact.TypeValue phone : phones) {
				phoneValue = phone.getValue();
				if(TextUtils.isEmpty(phoneValue)) {
					continue;
				}

				if(!phoneValue.startsWith("+")) {
					phoneValue = String.format("+%s%s", 0, phoneValue);
				}

				switch (phoneTypes[phone.getType()]) {
					case HOME_PHONE:
						cloudInfo.homePhone = phoneValue;
						break;
					case HOME_PHONE2:
						cloudInfo.homePhone2 = phoneValue;
						break;
					case BUSINESS_PHONE:
						cloudInfo.businessPhone = phoneValue;
						break;
					case BUSINESS_PHONE2:
						cloudInfo.businessPhone2 = phoneValue;
						break;
					case MOBILE_PHONE:
						cloudInfo.mobilePhone = phoneValue;
						break;
					case BUSINESS_FAX:
						cloudInfo.businessFax = phoneValue;
						break;
					case COMPANY_PHONE:
						cloudInfo.companyPhone = phoneValue;
						break;
					case ASSISTANT_PHONE:
						cloudInfo.assistantPhone = phoneValue;
						break;
					case CAR_PHONE:
						cloudInfo.carPhone = phoneValue;
						break;
					case OTHER_PHONE:
						cloudInfo.otherPhone = phoneValue;
						break;
					case OTHER_FAX:
						cloudInfo.otherFax = phoneValue;
						break;
					case CALLBACK_PHONE:
						cloudInfo.callbackPhone = phoneValue;
						break;
					default:
						break;
				}
			}

			//translate emails
			List<Contact.TypeValue> emails = contact.getEmails();
			CloudPersonalContact.EmailType[] emailTypes = CloudPersonalContact.EmailType.values();
			for (Contact.TypeValue email : emails) {
				switch (emailTypes[email.getType()]) {
					case EMAIL:
						cloudInfo.email = email.getValue();
						break;
					case EMAIL2:
						cloudInfo.email2 = email.getValue();
						break;
					case EMAIL3:
						cloudInfo.email3 = email.getValue();
						break;
					default:
						break;
				}
			}

			//translate addresses
			List<Contact.TypeAddress> addresses = contact.getAddresses();
			CloudPersonalContact.AddressType[] addressTypes = CloudPersonalContact.AddressType.values();
			for (Contact.TypeAddress address : addresses) {
				switch (addressTypes[address.getType()]) {
					case BUSINESS_ADDRESS:
						cloudInfo.businessAddress = address.getValue();
						break;
					case HOME_ADDRESS:
						cloudInfo.homeAddress = address.getValue();
						break;
					case OTHER_ADDRESS:
						cloudInfo.otherAddress = address.getValue();
						break;
					default:
						break;
				}
			}
		}catch (Throwable th) {
			MktLog.e(TAG, "translateToCloudPersonalContactInfo: error=" + th.toString());
		}

		return cloudInfo;
	}

	public static List<Contact.TypeValue> orderedPhones(List<Contact.TypeValue> phones) {
//		CloudPhoneManager cloudPhoneManager = new CloudPhoneManager();
//		categorizePhones(cloudPhoneManager, phones);
//		return addUnorderedPhones(cloudPhoneManager, new ArrayList<String>(), false);
		return phones;
	}

	public static class TranslateResult {
		public static final int PHONE_OUT_OF_ARRANGE = 0x0001;
		public static final int EMAIL_OUT_OF_ARRANGE = 0x0010;
		public static final int ADDRESS_OUT_OF_ARRANGE = 0x0100;
		public static final int WEBPAGE_OUT_OF_ARRANGE = 0x1000;
		public int flag = 0;
	}



	public static CloudPersonalContact translateDeviceContactToCloud(long contactId, TranslateResult result) {
		CloudPersonalContact cloudContact = null;
		Contact contact = ContactsProvider.getInstance().getContact(Contact.ContactType.DEVICE, contactId, true);
		if(contact != null) {
			DeviceContact deviceContact = (DeviceContact)contact;
			cloudContact = new CloudPersonalContact(CloudPersonalContact.CloudContactType.LOCAL, CloudPersonalContact.getLocalContactId(),
					deviceContact.getDisplayName(),
					//When import a device contact to cloud, if current contact doesn't have any one of those four fields: Add "display name" into the "first name" field.
					validate(deviceContact.getFirstName(), deviceContact.getMiddleName(), deviceContact.getCompany(), deviceContact.getE164PhoneNumbers())? deviceContact.getFirstName() : deviceContact.getDisplayName(),
					deviceContact.getMiddleName(), deviceContact.getLastName(), deviceContact.getNickName(),
					deviceContact.getCompany(), deviceContact.getJobTitle());
			cloudContact.setBirthday(deviceContact.getBirthday());

			//set web page
			List<String> webPages = deviceContact.getWebPages();
			int sizeOfWebPage = webPages.size();
			if(sizeOfWebPage > 0) {
				cloudContact.addWebPage(webPages.get(0));
				if(sizeOfWebPage > 1) {
					result.flag |= TranslateResult.WEBPAGE_OUT_OF_ARRANGE;
				}
			}

			cloudContact.setNotes(deviceContact.getNotes());

			//map to cloud contact phone type
			List<Contact.TypeValue> deviceOriginalPhones = deviceContact.getOriginalPhoneNumbers();
			List<String> restPhones = new ArrayList<>();
			CloudPhoneManager cloudPhoneManager = new CloudPhoneManager();
			StringArray currentPhones;

            /*
            List<Contact.TypeValue> validateDevicePhones = new ArrayList<>();
            for (Contact.TypeValue phone : deviceOriginalPhones) {
                //normalize phone number
                String phoneNumber = phoneNumberNormalize(phone.getValue());
                if(!TextUtils.isEmpty(phoneNumber)) {
                    validateDevicePhones.add(phone);
                }
            }*/

			for (Contact.TypeValue phone : deviceOriginalPhones) {
				switch (phone.getType()) {
					case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
						currentPhones = cloudPhoneManager.homePhones;
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
						currentPhones = cloudPhoneManager.mobilePhones;
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
					case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
						currentPhones = cloudPhoneManager.businessPhones;
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
					case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
						currentPhones = cloudPhoneManager.faxPhones;
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
						currentPhones = cloudPhoneManager.assistantPhones;
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
						currentPhones = cloudPhoneManager.carPhones;
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
						currentPhones = cloudPhoneManager.companyPhones;
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
					case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
					case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
					case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
					case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
					case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
					case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
					case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
					case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
					case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
					case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
					default:
						currentPhones = cloudPhoneManager.otherPhones;
						break;
				}

				if(!currentPhones.addData(phone.getValue())) {
					restPhones.add(phone.getValue());
				}
			}

			List<Contact.TypeValue> cloudOriginalPhones = addUnorderedPhones(cloudPhoneManager, restPhones, false);
			if(deviceOriginalPhones.size() > cloudOriginalPhones.size()) {
				result.flag |= TranslateResult.PHONE_OUT_OF_ARRANGE;
			}

			cloudContact.setOriginalPhoneNumbers(cloudOriginalPhones);
			cloudContact.setE164PhoneNumbers(cloudOriginalPhones);

			//email type map, no rule
			List<Contact.TypeValue> deviceEmails = deviceContact.getEmailAddressList();
			List<Contact.TypeValue> validateDeviceEmails = new ArrayList<>();
			for(Contact.TypeValue email : deviceEmails) {
				//filter invalid email address
				if(validateEmail(email.getValue())) {
					validateDeviceEmails.add(email);
				}
			}

			List<Contact.TypeValue> cloudEmails = new ArrayList<>();
			CloudPersonalContact.EmailType[] emailTypes = CloudPersonalContact.EmailType.values();
			for(int i = 0; i< emailTypes.length && i < validateDeviceEmails.size(); i++) {
				//filter invalid email address
				cloudEmails.add(new Contact.TypeValue(emailTypes[i].ordinal(), validateDeviceEmails.get(i).getValue()));
			}

			if(validateDeviceEmails.size() > cloudEmails.size()) {
				result.flag |= TranslateResult.EMAIL_OUT_OF_ARRANGE;
			}
			cloudContact.setEmails(cloudEmails);

			//address type map
			List<Contact.TypeAddress> deviceAddresses = deviceContact.getAddresses();
			List<Contact.TypeAddress> validateDeviceAddress = new ArrayList<>();
			for(Contact.TypeAddress address : deviceAddresses) {
				//filter invalid email address
				//1, remove invalid char
				Address a = new Address(address.getValue().getCountry(), address.getValue().getState(), address.getValue().getCity(),
						address.getValue().getStreet(), getValidZipCode(address.getValue().getZip()) );
				if(!TextUtils.isEmpty(a.toString())) {
					validateDeviceAddress.add(new Contact.TypeAddress(address.getType(), a));
				}

			}
			List<Contact.TypeAddress> restAddresses = new ArrayList<>();
			List<Contact.TypeAddress> cloudAddresses = new ArrayList<>();
			AddressArray homeAddress = new AddressArray(CloudPersonalContact.HOME_ADDRESS_SIZE);
			AddressArray workAddress = new AddressArray(CloudPersonalContact.WORK_ADDRESS_SIZE);
			AddressArray otherAddress = new AddressArray(CloudPersonalContact.OTHER_ADDRESS_SIZE);
			AddressArray currentAddress;
			for(int i= 0; i < validateDeviceAddress.size(); i++) {
				Contact.TypeAddress address = validateDeviceAddress.get(i);
				switch (address.getType()) {
					case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
						currentAddress = homeAddress;
						break;
					case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK:
						currentAddress = workAddress;
						break;
					case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER:
						currentAddress = otherAddress;
						break;
					default:
						currentAddress = otherAddress;
						break;
				}

				if(!currentAddress.addData(address)) {
					restAddresses.add(address);
				}
			}

			for (Contact.TypeAddress address : restAddresses) {
				if(homeAddress.addData(address)) {
					continue;
				}

				if(workAddress.addData(address)) {
					continue;
				}

				if(otherAddress.addData(address)) {
					continue;
				}
			}

			List<Contact.TypeAddress> listAddress = homeAddress.getData();
			for(int i=0; i< listAddress.size(); i++ ) {
				cloudAddresses.add(new Contact.TypeAddress(CloudPersonalContact.ADDRESS_TYPE_HOME[i], listAddress.get(i).getValue()));
			}

			listAddress = workAddress.getData();
			for(int i=0; i< listAddress.size(); i++ ) {
				cloudAddresses.add(new Contact.TypeAddress(CloudPersonalContact.ADDRESS_TYPE_WORK[i], listAddress.get(i).getValue()));
			}

			listAddress = otherAddress.getData();
			for(int i=0; i< listAddress.size(); i++ ) {
				cloudAddresses.add(new Contact.TypeAddress(CloudPersonalContact.ADDRESS_TYPE_OTHER[i], listAddress.get(i).getValue()));
			}

			if(validateDeviceAddress.size() > cloudAddresses.size()) {
				result.flag |= TranslateResult.ADDRESS_OUT_OF_ARRANGE;
			}
			cloudContact.setAddress(cloudAddresses);
			cloudContact.generateDisplayName();
		}
		return cloudContact;
	}

	public static Contact importFromCachedDeviceContactToCloud(Context context, long contactId) {
		ContactsUtils.TranslateResult translateResult = new ContactsUtils.TranslateResult();
		final CloudPersonalContact contact = ContactsUtils.translateDeviceContactToCloud(contactId, translateResult);
		if(contact != null && !contact.isEmpty()) {
			//add to cloud contact and sync
			ContactsProvider.getInstance().addContact(contact);
		}
		return contact;
	}


	public static class CloudPhoneManager {
		StringArray mobilePhones = new StringArray(CloudPersonalContact.MOBILE_PHONE_SIZE);
		StringArray businessPhones = new StringArray(CloudPersonalContact.BUSINESS_PHONE_SIZE);
		StringArray homePhones = new StringArray(CloudPersonalContact.HOME_PHONE_SIZE);
		StringArray companyPhones = new StringArray(CloudPersonalContact.COMPANY_PHONE_SIZE);
		StringArray faxPhones = new StringArray(CloudPersonalContact.FAX_PHONE_SIZE);
		StringArray assistantPhones = new StringArray(CloudPersonalContact.ASSISTANT_PHONE_SIZE);
		StringArray carPhones = new StringArray(CloudPersonalContact.CAR_PHONE_SIZE);
		StringArray otherPhones = new StringArray(CloudPersonalContact.OTHER_PHONE_SIZE);
		public CloudPhoneManager() {}
	}


	public static String phoneNumberNormalize(String source) {
		if(TextUtils.isEmpty(source)) {
			return "";
		}

		return phoneNumberNormalize(source, 0, source.length());
	}

	private static List<Contact.TypeValue> addUnorderedPhones(CloudPhoneManager cloudPhoneManager, List<String> phoneNumbers, boolean isFax) {
		List<Contact.TypeValue> cloudPhones = new ArrayList<>();
		for (String phone : phoneNumbers) {
			phone = phoneNumberNormalize(phone);
			if(TextUtils.isEmpty(phone)) {
				continue;
			}

			//when is fax, it will get the highest priority
			if(isFax) {
				if(cloudPhoneManager.faxPhones.addData(phone)) {
					continue;
				}
			}

			if(cloudPhoneManager.mobilePhones.addData(phone)) {
				continue;
			}

			if(cloudPhoneManager.businessPhones.addData(phone)) {
				continue;
			}

			if(cloudPhoneManager.homePhones.addData(phone)) {
				continue;
			}

			if(cloudPhoneManager.companyPhones.addData(phone)) {
				continue;
			}

			if(!isFax) {
				if (cloudPhoneManager.faxPhones.addData(phone)) {
					continue;
				}
			}

			if(cloudPhoneManager.assistantPhones.addData(phone)) {
				continue;
			}

			if(cloudPhoneManager.carPhones.addData(phone)) {
				continue;
			}

			if(cloudPhoneManager.otherPhones.addData(phone)) {
				continue;
			}
		}

		//map to cloud contact phone type
		List<String> phoneList = cloudPhoneManager.mobilePhones.getData();
		for(int i=0; i< phoneList.size(); i++ ) {
			cloudPhones.add(new Contact.TypeValue(CloudPersonalContact.PHONE_TYPE_MOBILE[i], phoneList.get(i)));
		}

		phoneList = cloudPhoneManager.businessPhones.getData();
		for(int i=0; i< phoneList.size(); i++ ) {
			cloudPhones.add(new Contact.TypeValue(CloudPersonalContact.PHONE_TYPE_BUSINESS[i], phoneList.get(i)));
		}

		phoneList = cloudPhoneManager.homePhones.getData();
		for(int i=0; i< phoneList.size(); i++ ) {
			cloudPhones.add(new Contact.TypeValue(CloudPersonalContact.PHONE_TYPE_HOME[i], phoneList.get(i)));
		}

		phoneList = cloudPhoneManager.companyPhones.getData();
		for(int i=0; i< phoneList.size(); i++ ) {
			cloudPhones.add(new Contact.TypeValue(CloudPersonalContact.PHONE_TYPE_COMPANY[i], phoneList.get(i)));
		}

		phoneList = cloudPhoneManager.faxPhones.getData();
		for(int i=0; i< phoneList.size(); i++ ) {
			cloudPhones.add(new Contact.TypeValue(CloudPersonalContact.PHONE_TYPE_FAX[i], phoneList.get(i)));
		}

		phoneList = cloudPhoneManager.assistantPhones.getData();
		for(int i=0; i< phoneList.size(); i++ ) {
			cloudPhones.add(new Contact.TypeValue(CloudPersonalContact.PHONE_TYPE_ASSISTANT[i], phoneList.get(i)));
		}

		phoneList = cloudPhoneManager.carPhones.getData();
		for(int i=0; i< phoneList.size(); i++ ) {
			cloudPhones.add(new Contact.TypeValue(CloudPersonalContact.PHONE_TYPE_CAR[i], phoneList.get(i)));
		}

		phoneList = cloudPhoneManager.otherPhones.getData();
		for(int i=0; i< phoneList.size(); i++ ) {
			cloudPhones.add(new Contact.TypeValue(CloudPersonalContact.PHONE_TYPE_OTHER[i], phoneList.get(i)));
		}

		return cloudPhones;
	}

}
