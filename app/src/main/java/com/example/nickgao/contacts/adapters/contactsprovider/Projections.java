/**
 * Copyright (C) 2010-2011, RingCentral, Inc. 
 * All Rights Reserved.
 */

package com.example.nickgao.contacts.adapters.contactsprovider;

import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;

import com.example.nickgao.androidsample11.ContactsUtils;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.database.RCMDataStore.*;

/**
 * TODO Re-factor (remove)
 */
public final class Projections {

    /**
     * This class help us to build projection for ListActivity
     * to display summary information about personal contacts.
     * Help to parse Cursor
     */
    public static final class Personal {

        private Personal() {
        }

        /**
         * Contacts projection.
         */
        public static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
                People._ID,                                // 0
                People.DISPLAY_NAME,                        // 1
                People.STARRED,                                // 2
                ContactsUtils.People_Has_Phone_Number,        // 3
                ContactsUtils.People_PhotoID,                // 4
        };

        /**
         * Contacts group projection.
         */
        public static final String[] CONTACTS_GROUP_SUMMARY_PROJECTION = new String[] {
                People._ID,
                People.DISPLAY_NAME,                        // 1
                People.STARRED,                                // 2
                Data.CONTACT_ID,                            // 3
                ContactsUtils.People_PhotoID,                // 4
                Data.RAW_CONTACT_ID,                        // 5
        };

        /**
         * Person ID column index.
         */
        public static final int ID_COLUMN_INDEX = 0;

        /**
         * Person name column index.
         */
        public static final int NAME_COLUMN_INDEX = 1;

        /**
         * Person stared column index.
         */
        public static final int STARRED_COLUMN_INDEX = 2;

        /**
         * Person stared column index.
         */
        public static final int HAS_PHONE_COLUMN_INDEX = 3;

        /**
         * Person photo data column index.
         */
        public static final int PHOTO_COLUMN_INDEX = 4;

        /**
         * Person phone number column index.
         */
        public static final int NUMBER_COLUMN_INDEX = 5;

    }

    public static final class PersonalFilter {

        private PersonalFilter() {
        }

        /**
         * Person ID column index.
         */
        public static final int ID_COLUMN_INDEX = 0;

        /**
         * Person name column index.
         */
        public static final int NAME_COLUMN_INDEX = 1;

        /**
         * Person stared column index.
         */
        public static final int STARRED_COLUMN_INDEX = 2;

        /**
         * Person photo data column index.
         */
        public static final int HAS_PHONE_COLUMN_INDEX = 3;

        /**
         * Person photo data column index.
         */
        public static final int PHOTO_COLUMN_INDEX = 4;

        /**
         * Person phone number column index.
         */
        public static final int NUMBER_COLUMN_INDEX = 5;

        /**
         * Person phone number column index.
         */
        public static final int TYPE_COLUMN_INDEX = 6;
    }

    public static final class PersonalPhones {

        private PersonalPhones() {
        }

        /**
         * The projection to use when querying the phones table
         */
        public static final String[] PHONES_PROJECTION = new String[] {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.STARRED,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };

        /**
         * Person ID column index.
         */
        public static final int ID_COLUMN_INDEX = 0;

        /**
         * Person ID column index.
         */
        public static final int CONTACT_ID_COLUMN_INDEX = 1;

        /**
         * Person name column index.
         */
        public static final int NAME_COLUMN_INDEX = 2;

        /**
         * Person stared column index.
         */
        public static final int STARRED_COLUMN_INDEX = 3;

        /**
         * Person photo data column index.
         */
        public static final int PHOTO_COLUMN_INDEX = 4;

        /**
         * Person phone number column index.
         */
        public static final int NUMBER_COLUMN_INDEX = 5;

        /**
         * Person phone number type column index.
         */
        public static final int TYPE_COLUMN_INDEX = 6;
    }

    /**
     * This class help us to build projection for ListActivity
     * to display summary information about extensions (company contacts)
     * Help to parse Cursor
     */
    public static final class Extensions {

        private Extensions() {
        }

        /**
         * Extension projection.
         */
        public static final String[] EXTENSION_SUMMARY_PROJECTION = new String[] {
                ExtensionsTable._ID,                        // 0
                ExtensionsTable.MAILBOX_ID,                    // 1
                ExtensionsTable.RCM_DISPLAY_NAME,            // 2
                ExtensionsTable.RCM_STARRED,                // 3
                ExtensionsTable.JEDI_PIN,                    // 4
                ExtensionsTable.JEDI_EMAIL,                // 5
                ExtensionsTable.JEDI_FIRST_NAME,            // 6
                ExtensionsTable.JEDI_LAST_NAME,                // 7
                ExtensionsTable.JEDI_MAILBOX_ID_EXT,        // 8
                ExtensionsTable.JEDI_CONTACT_PHONE,         // 9
                ExtensionsTable.JEDI_MOBILE_PHONE           // 10
        };

        /**
         * Extension ID column index.
         */
        public static final int EXT_ID_COLUMN_INDEX = 0;

        /**
         * Extension mailbox ID column index.
         */
        public static final int EXT_MAILBOX_ID_COLUMN_INDEX = 1;

        /**
         * Extension name column index.
         */
        public static final int EXT_NAME_COLUMN_INDEX = 2;

        /**
         * Extension stared column index.
         */
        public static final int EXT_STARRED_COLUMN_INDEX = 3;

        /**
         * Extension pin column index.
         */
        public static final int EXT_PIN_COLUMN_INDEX = 4;

        /**
         * Extension email column index.
         */
        public static final int EXT_EMAIL_COLUMN_INDEX = 5;

        /**
         * Extension name column index.
         */
        public static final int EXT_FIRST_NAME_COLUMN_INDEX = 6;

        /**
         * Extension name column index.
         */
        public static final int EXT_LAST_NAME_COLUMN_INDEX = 7;

        /**
         * Extension mailbox ID column index
         */
        public static final int EXT_MAILBOX_ID_EXT_COLUMN_INDEX = 8;


        /**
         * Extension mobile phone column index
         */
        public static final int EXT_MOBILE_PHONE_COLUMN_INDEX = 10;
    }

    /**
     * This class help us to build projection for ListActivity
     * to display summary information about personal contacts.
     * Help to parse Cursor
     */
    public static final class CallerID {

        private CallerID() {
        }



        /**
         * CallerId ID column index.
         */
        public static final int ID_COLUMN_INDEX = 0;

        /**
         * Number column index.
         */
        public static final int NUMBER_COLUMN_INDEX = 1;

        /**
         * Type(label) column index.
         */
        public static final int TYPE_COLUMN_INDEX = 2;

        /**
         * Mark selected column index.
         */
        public static final int SELECTED_COLUMN_INDEX = 3;

    }

    public static final class SelectContact {
        public static final int ID_COLUMN_INDEX = 0;
        public static final int DISPLAY_NAME_COLUMN_INDEX = 1;
        public static final int PHONE_NUMBER_COLUMN_INDEX = 2;
        public static final int PHONE_TYPE_COLUMN_INDEX = 3;
        public static final int PHONE_ID_COLUMN_INDEX = 4;
        public static final int CONTACT_ID_COLUMN_INDEX = 5;
        public static final int LOOKUP_KEY_COLUMN_INDEX = 6;
        public static final int CONTACT_TYPE = 7;
    }

    public static final class PersonalCloudContacts {
        public enum PERSONAL_NUMBER_INFO_PROJECTION {
            ID {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalPhoneNumberTable.ID;
                }
            },
            PHONE_NUMBER {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalPhoneNumberTable.PHONE_NUMBER;
                }
            },
            PHONE_TYPE {
                @Override
                public String toString() { return RCMDataStore.PersonalPhoneNumberTable.PHONE_TYPE; }
            },
        }

        public enum PERSONAL_EMAIL_INFO_PROJECTION {
            ID {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalEmailTable.ID;
                }
            },
            EMAIL {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalEmailTable.EMAIL;
                }
            },
            TYPE {
                @Override
                public String toString() { return RCMDataStore.PersonalEmailTable.TYPE; }
            },
        }

        public enum PERSONAL_ADDRESS_INFO_PROJECTION {
            ID {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalAddressTable.ID;
                }
            },
            ADDRESS_COUNTRY {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalAddressTable.ADDRESS_COUNTRY;
                }
            },
            ADDRESS_STATE {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalAddressTable.ADDRESS_STATE;
                }
            },
            ADDRESS_CITY {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalAddressTable.ADDRESS_CITY;
                }
            },

            ADDRESS_STREET {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalAddressTable.ADDRESS_STREET;
                }
            },

            ADDRESS_ZIP {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalAddressTable.ADDRESS_ZIP;
                }
            },

            ADDRESS_TYPE {
                @Override
                public String toString() { return RCMDataStore.PersonalAddressTable.ADDRESS_TYPE; }
            },
        }

        public enum PERSONAL_CONTACT_ITEM_PROJECTION {
            ID {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.ID;
                }
            },
            URI {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.URI;
                }
            },
            AVAILABILITY {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.AVAILABILITY;
                }
            },
            DISPLAY_NAME {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.DISPLAY_NAME;
                }
            },
            FIRST_NAME {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.FIRST_NAME;
                }
            },
            LAST_NAME {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.LAST_NAME;
                }
            },
            MIDDLE_NAME {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.MIDDLE_NAME;
                }
            },
            NICK_NAME {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.NICK_NAME;
                }
            },
            COMPANY {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.COMPANY;
                }
            },
            JOB_TITLE {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.JOB_TITLE;
                }
            },

            BIRTHDAY {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.BIRTHDAY;
                }
            },

            WEB_PAGE {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.WEB_PAGE;
                }
            },

            NOTES {
                @Override
                public String toString() {
                    return RCMDataStore.PersonalContactsTable.NOTES;
                }
            },

            SYNC_STATUS {
                @Override
                public String toString() { return RCMDataStore.PersonalContactsTable.SYNC_STATUS;}
            }
        }

        /*
        public enum TEMP_NEW_PERSONAL_CONTACT_PROJECTION {
            ID {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable._ID;
                }
            },
            DISPLAY_NAME {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.DISPLAY_NAME;
                }
            },
            FIRST_NAME {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.FIRST_NAME;
                }
            },
            LAST_NAME {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.LAST_NAME;
                }
            },
            MIDDLE_NAME {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.MIDDLE_NAME;
                }
            },
            NICK_NAME {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.NICK_NAME;
                }
            },
            COMPANY {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.COMPANY;
                }
            },
            JOB_TITLE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.JOB_TITLE;
                }
            },
            HOME_PHONE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.HOME_PHONE;
                }
            },
            HOME_PHONE2 {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.HOME_PHONE2;
                }
            },
            BUSINESS_PHONE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.BUSINESS_PHONE;
                }
            },
            BUSINESS_PHONE2 {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.BUSINESS_PHONE2;
                }
            },
            MOBILE_PHONE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.MOBILE_PHONE;
                }
            },
            BUSINESS_FAX {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.BUSINESS_FAX;
                }
            },
            COMPANY_PHONE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.COMPANY_PHONE;
                }
            },
            ASSISTANT_PHONE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.ASSISTANT_PHONE;
                }
            },
            CAR_PHONE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.CAR_PHONE;
                }
            },
            OTHER_PHONE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.OTHER_PHONE;
                }
            },
            OTHER_FAX {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.OTHER_FAX;
                }
            },
            CALLBACK_PHONE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.CALLBACK_PHONE;
                }
            },
            EMAIL {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.EMAIL;
                }
            },
            EMAIL2 {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.EMAIL2;
                }
            },
            EMAIL3 {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.EMAIL3;
                }
            },
            HOME_ADDRESS_COUNTRY {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.HOME_ADDRESS_COUNTRY;
                }
            },
            HOME_ADDRESS_STATE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.HOME_ADDRESS_STATE;
                }
            },
            HOME_ADDRESS_CITY {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.HOME_ADDRESS_CITY;
                }
            },
            HOME_ADDRESS_STREET {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.HOME_ADDRESS_STREET;
                }
            },
            HOME_ADDRESS_ZIP {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.HOME_ADDRESS_ZIP;
                }
            },
            BUSINESS_ADDRESS_COUNTRY {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.BUSINESS_ADDRESS_COUNTRY;
                }
            },
            BUSINESS_ADDRESS_STATE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.BUSINESS_ADDRESS_STATE;
                }
            },
            BUSINESS_ADDRESS_CITY {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.BUSINESS_ADDRESS_CITY;
                }
            },
            BUSINESS_ADDRESS_STREET {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.BUSINESS_ADDRESS_STREET;
                }
            },
            BUSINESS_ADDRESS_ZIP {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.BUSINESS_ADDRESS_ZIP;
                }
            },
            OTHER_ADDRESS_COUNTRY {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.OTHER_ADDRESS_COUNTRY;
                }
            },
            OTHER_ADDRESS_STATE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.OTHER_ADDRESS_STATE;
                }
            },
            OTHER_ADDRESS_CITY {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.OTHER_ADDRESS_CITY;
                }
            },
            OTHER_ADDRESS_STREET {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.OTHER_ADDRESS_STREET;
                }
            },

            OTHER_ADDRESS_ZIP {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.OTHER_ADDRESS_ZIP;
                }
            },

            BIRTHDAY {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.BIRTHDAY;
                }
            },

            WEB_PAGE {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.WEB_PAGE;
                }
            },

            NOTES {
                @Override
                public String toString() {
                    return RCMDataStore.TempNewPersonalContactsTable.NOTES;
                }
            },
        }*/

        public static final String[] ONLY_ID_PROJECTION = new String[] {
                RCMDataStore.PersonalContactsTable.ID,              // 0
        };

        public static final int ID_COLUMN = 0;
    }
}
