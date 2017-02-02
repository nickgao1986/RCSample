package com.example.nickgao.datastore.extensioninfo;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.database.RCMProviderHelper;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.service.model.contact.Contact;
import com.example.nickgao.service.model.extensioninfo.ExtensionInfoResponse;
import com.example.nickgao.service.model.extensioninfo.ExtensionLanguage;
import com.example.nickgao.service.model.extensioninfo.RegionalSettings;
import com.example.nickgao.service.model.extensioninfo.ServiceFeatures;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by nick.gao on 2/1/17.
 */

public class ExtensionInfoDataStore {

    public static final String LOG_TAG = "[RC]ExtensionInfoDataStore";
    protected static String logTag = "[RC]ExtensionInfoDataStore";
    private static long mailboxId;
    private  static final String COMPANY_EXTENSION = "101";
    protected interface FeatureDBHandle {
        boolean isEnabled(Context context, long mailboxId);

        void setEnabled(Context context, long mailboxId, boolean enabled);
    }

    protected static final HashMap<String, FeatureDBHandle> sKnownFeatures = new HashMap<String, FeatureDBHandle>();
    protected static HashMap<String, Boolean> features;

    public static void storeExtensionInfo(ExtensionInfoResponse mResponseData) {
        initKnownFeatures();
        ExtensionInfo info = new ExtensionInfo();
        info.id = mResponseData.getId();
        info.extensionNumber = mResponseData.getExtensionNumber();
        info.name = mResponseData.getName();
        info.type = mResponseData.getType();
        info.status = mResponseData.getStatus();
        info.setupWizardState = mResponseData.getSetupWizardState();
        info.contactInfo = mResponseData.getContact();
        info.regionalSettings = mResponseData.getRegionalSettings();
//        String extension = RCMProviderHelper.getLoginExt(RingCentralApp.getContextRC());

//        if(info.type != null && info.type.equals(RCMDataStore.AccountInfoTable.EXTENSION_TYPE_DEPARTMENT)) {
//            MktLog.i(LOG_TAG,"is dept user");
//            info.accountinfo_agent = true;
//        }

        if (mResponseData.getDepartments() != null) {
            //           for(int i=0; i < mResponseData.getDepartments().length; i++) {
//                Departments departments = mResponseData.getDepartments()[i];
//                if(extension.equalsIgnoreCase(departments.getExtensionNumber())) {
//                    info.accountinfo_agent = true;
//                    MktLog.i(LOG_TAG,"service extension is agent");
//                }
//            }
            if (mResponseData.getDepartments().length > 0) {
                info.accountinfo_agent = true;
                MktLog.i(LOG_TAG, "service extension is agent");
            }

        }

        if (mResponseData.getRegionalSettings() != null && mResponseData.getRegionalSettings().getHomeCountry() != null) {
            info.isoCode = mResponseData.getRegionalSettings().getHomeCountry().getIsoCode();
        }

//        if (mResponseData.getPermissions() != null && mResponseData.getPermissions().getAdmin() != null) {
//            info.isAdmin = mResponseData.getPermissions().getAdmin().enabled;
//            if (BUILD.TRACES_TO_LOGCAT_ENABLED) {
//                MktLog.i(LOG_TAG, "it's admin account");
//            }
//        }
        Context context = RingCentralApp.getContextRC();
        mailboxId = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
//        if(GeneralSettings.getSettings().isSSOLogin()) {
//            if(!COMPANY_EXTENSION.equals(info.extensionNumber)) {
//                RCMProviderHelper.saveLoginExt(context, info.extensionNumber);
//            }
//        }

        //store some of extension data to AccountInfo table
        storeForAccountInfo(RingCentralApp.getContextRC(), info);

        parseFeature(mResponseData);
        storeFeature();
        storeData(info, context);

    }


    static class ExtensionInfo {
        String id;
        String extensionNumber;
        String name;
        Contact contactInfo;
        String type;
        String status;
        String isoCode;
        boolean accountinfo_agent;

        ArrayList<String> deptExtensionNumberList;
        String setupWizardState;
        boolean isAdmin;
        boolean internationalCallingEnabled;
        RegionalSettings regionalSettings;

        public ExtensionInfo() {

        }
    }

    private static void storeData(ExtensionInfo ei, Context context) {

        ContentValues values = new ContentValues();
        values.put(RCMDataStore.ServiceExtensionInfoTable.EXTENSION_INFO_ID, ei.id);
        if (!TextUtils.isEmpty(ei.type)) {
            ei.type = StringUtils.deleteWhitespace(ei.type);
            values.put(RCMDataStore.ServiceExtensionInfoTable.EXTENSION_INFO_TYPE, ei.type);
        }

        values.put(RCMDataStore.ServiceExtensionInfoTable.EXTENSION_INFO_STATUS, ei.status);
        values.put(RCMDataStore.ServiceExtensionInfoTable.EXTENSION_INFO_EXT_NUMBER, ei.extensionNumber);
//        if (ei.contactInfo != null) {
//            values.put(RCMDataStore.ServiceExtensionInfoTable.EXTENSION_INFO_FIRST_NAME, ei.contactInfo.getFirstName());
//            values.put(RCMDataStore.ServiceExtensionInfoTable.EXTENSION_INFO_LAST_NAME, ei.contactInfo.getLastName());
//            values.put(RCMDataStore.ServiceExtensionInfoTable.EXTENSION_INFO_COMPANY, ei.contactInfo.getCompany());
//            values.put(RCMDataStore.ServiceExtensionInfoTable.EXTENSION_INFO_EMAIL, ei.contactInfo.getEmail());
//        }
        values.put(RCMDataStore.ServiceExtensionInfoTable.EXTENSION_INFO_FULL_NAME, ei.name);
        values.put(RCMDataStore.ServiceExtensionInfoTable.EXTENSION_INFO_ISO_CODE, ei.isoCode);
        values.put(RCMDataStore.ServiceExtensionInfoTable.REST_IS_ADMIN, ei.isAdmin ? 1 : 0);
        if (ei.regionalSettings != null) {
            ExtensionLanguage language = ei.regionalSettings.getLanguage();
            if (language != null) {
                values.put(RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_ID, String.valueOf(language.getId()));
                values.put(RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_NAME, language.getName());
                values.put(RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_LOCALE, language.getLocaleCode());
            }

            language = ei.regionalSettings.getGreetingLanguage();
            if (language != null) {
                values.put(RCMDataStore.ServiceExtensionInfoTable.USER_GREETING_LANGUAGE_ID, String.valueOf(language.getId()));
                values.put(RCMDataStore.ServiceExtensionInfoTable.USER_GREETING_LANGUAGE_NAME, language.getName());
                values.put(RCMDataStore.ServiceExtensionInfoTable.USER_GREETING_LANGUAGE_LOCALE, language.getLocaleCode());
            }

            language = ei.regionalSettings.getFormattingLocale();
            if (language != null) {
                values.put(RCMDataStore.ServiceExtensionInfoTable.USER_FORMATTING_LANGUAGE_ID, String.valueOf(language.getId()));
                values.put(RCMDataStore.ServiceExtensionInfoTable.USER_FORMATTING_LANGUAGE_NAME, language.getName());
                values.put(RCMDataStore.ServiceExtensionInfoTable.USER_FORMATTING_LANGUAGE_LOCALE, language.getLocaleCode());
            }
        }

//        if (!RCMProviderHelper.isServiceInfoTableHasData(context)) {
//            context.getContentResolver().insert(UriHelper.getUri(RCMProvider.SERVICE_EXTENSION_INFO),
//                    values);
//        } else {
//            int count = context.getContentResolver().update(UriHelper.getUri(RCMProvider.SERVICE_EXTENSION_INFO, mailboxId),
//                    values,
//                    null, null);
//            MktLog.i(LOG_TAG, "serviceinfo table update count=" + count);
//        }

    }

    private static void storeForAccountInfo(Context context, ExtensionInfo ei) {
        ContentValues values = new ContentValues();
        values.put(RCMDataStore.AccountInfoTable.MAILBOX_ID, CurrentUserSettings.getSettings(context).getCurrentMailboxId());
        values.put(RCMDataStore.AccountInfoTable.RCM_LOGIN_NUMBER, RCMProviderHelper.getLoginNumber(context));
        values.put(RCMDataStore.AccountInfoTable.RCM_LOGIN_EXT, RCMProviderHelper.getLoginExt(context));
        values.put(RCMDataStore.AccountInfoTable.RCM_PASSWORD, "");

//        values.put(RCMDataStore.AccountInfoTable.JEDI_FIRST_NAME, ei.contactInfo.getFirstName());
//        values.put(RCMDataStore.AccountInfoTable.JEDI_LAST_NAME, ei.contactInfo.getLastName());
//        values.put(RCMDataStore.AccountInfoTable.JEDI_EMAIL, ei.contactInfo.getEmail());

        values.put(RCMDataStore.AccountInfoTable.JEDI_PIN, ei.extensionNumber);

        if (ei.isAdmin) {
            values.put(RCMDataStore.AccountInfoTable.JEDI_ACCESS_LEVEL, RCMDataStore.AccountInfoTable.ACCESS_LEVEL_ADMIN);
        } else {
            values.put(RCMDataStore.AccountInfoTable.JEDI_ACCESS_LEVEL, RCMDataStore.AccountInfoTable.ACCESS_LEVEL_VIEW);
        }
        values.put(RCMDataStore.AccountInfoTable.JEDI_EXTENSION_TYPE, ei.type);

        values.put(RCMDataStore.AccountInfoTable.JEDI_SERVICE_VERSION, 3);
        //set agent type
        values.put(RCMDataStore.AccountInfoTable.JEDI_AGENT, ei.accountinfo_agent ? 1 : 0);
        values.put(RCMDataStore.AccountInfoTable.JEDI_SETUP_WIZARD_STATE, ei.setupWizardState);

//        if (RCMProviderHelper.isAccountInfoHasData(context)) {
//            MktLog.d(LOG_TAG, "storeForAccountInfo(); isAccountInfoHasData=true");
//            context.getContentResolver().update(UriHelper.getUri(RCMProvider.ACCOUNT_INFO,
//                    CurrentUserSettings.getSettings(context).getCurrentMailboxId()), values, null, null);
//        } else {
//            MktLog.d(LOG_TAG, "storeForAccountInfo(); isAccountInfoHasData=false");
//            context.getContentResolver().insert(UriHelper.getUri(RCMProvider.ACCOUNT_INFO), values);
//        }
    }

    private static void parseFeature(ExtensionInfoResponse mResponseData) {
        features = new HashMap<String, Boolean>();
        ServiceFeatures[] array = mResponseData.getServiceFeatures();
        for (int i = 0; i < array.length; i++) {
            ServiceFeatures feature = array[i];
            String featureName = feature.featureName;
            Boolean enabled = feature.enabled;

            if (!TextUtils.isEmpty(featureName)) {
                if (sKnownFeatures.containsKey(featureName.trim())) {
                    features.put(featureName, enabled);
                } else {
                    MktLog.w(LOG_TAG, logTag + " unknown feature " + featureName);
                }
            }

        }
    }

    private static void storeFeature() {
        Context context = RingCentralApp.getContextRC();
        Set<String> set = sKnownFeatures.keySet();
        for (String featureName : set) {

            if (LogSettings.ENGINEERING) {
                MktLog.d(LOG_TAG, "featureName : " + featureName);
            }

            if (!features.containsKey(featureName)) {
                MktLog.e(LOG_TAG, logTag + ".onCompletion: not received feature : " + featureName);
                continue;
            }
            Boolean newValue = features.get(featureName);
            if (newValue == null) {
                MktLog.e(LOG_TAG, logTag + ".onCompletion: received feature value is not valid : " + featureName);
                continue;
            }
            boolean enabled = newValue.booleanValue();
            FeatureDBHandle handle = sKnownFeatures.get(featureName);
            if (handle.isEnabled(context, mailboxId) != enabled) {
                handle.setEnabled(context, mailboxId, enabled);
                MktLog.w(LOG_TAG, logTag + ".onCompletion: Feature change state : \"" + featureName + "\" is "
                        + (enabled ? "ENABLED" : "DISABLED"));
            }
        }
    }


    protected static void initKnownFeatures() {
        sKnownFeatures.put("SMS", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isSMSFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setSMSFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("SMSReceiving", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isSMSReceivingFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setSMSReceivingFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("Pager", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isPagerFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setPagerFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("PagerReceiving", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isPagerReceivingFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setPagerReceivingFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("Voicemail", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isVoicemailFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setVoicemailFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("Fax", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isFaxFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setFaxFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("FaxReceiving", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isFaxReceivingFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setFaxReceivingFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("DND", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isDNDFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setDNDFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("RingOut", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isRingOutFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setRingOutFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("InternationalCalling", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isInternationalCallingFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setInternationalCallingFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("Presence", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isPresenceFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setPresenceFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("VideoConferencing", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isVideoFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setVideoFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("SalesForce", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isSalesForceFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setSalesForceFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("Intercom", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isIntercomFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setIntercomFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("Paging", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isPagingFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setPagingFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("Conferencing", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isConferencingFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setConferencingFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("VoipCalling", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isVoipCallingFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setVoipCallingFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("FreeSoftPhoneLines", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isFreeSoftPhoneLinesFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setFreeSoftPhoneLinesFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("HipaaCompliance", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isHipaaComplianceFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setHipaaComplianceFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("CallPark", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isCallParkFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setCallParkFeatureEnabled(context, mailboxId, enabled);
            }
        });

        sKnownFeatures.put("OnDemandCallRecording", new FeatureDBHandle() {
            public boolean isEnabled(Context context, long mailboxId) {
                return RCMProviderHelper.isCallRecordingFeatureEnabled(context, mailboxId);
            }

            public void setEnabled(Context context, long mailboxId, boolean enabled) {
                RCMProviderHelper.setCallRecordingFeatureEnabled(context, mailboxId, enabled);
            }
        });

//        sKnownFeatures.put("CallForwarding", new FeatureDBHandle() {
//            public boolean isEnabled(Context context, long mailboxId) {
//                return RCMProviderHelper.isCallForwardingFeatureEnabled(context, mailboxId);
//            }
//
//            public void setEnabled(Context context, long mailboxId, boolean enabled) {
//                RCMProviderHelper.setCallForwardingFeatureEnabled(context, mailboxId, enabled);
//            }
//        });
//
//        sKnownFeatures.put("Reports", new FeatureDBHandle() {
//            public boolean isEnabled(Context context, long mailboxId) {
//                return RCMProviderHelper.isReportFeatureEnabled(context, mailboxId);
//            }
//
//            public void setEnabled(Context context, long mailboxId, boolean enabled) {
//                RCMProviderHelper.setReportFeatureEnabled(context, mailboxId, enabled);
//            }
//        });
//
//        sKnownFeatures.put("SingleExtensionUI", new FeatureDBHandle() {
//            public boolean isEnabled(Context context, long mailboxId) {
//                return RCMProviderHelper.isSingleExtensionModeEnabled(context, mailboxId);
//            }
//
//            public void setEnabled(Context context, long mailboxId, boolean enabled) {
//                RCMProviderHelper.setSingleExtensionModeEnabled(context, mailboxId, enabled);
//            }
//        });
//
//        sKnownFeatures.put("EncryptionAtRest", new FeatureDBHandle() {
//            @Override
//            public boolean isEnabled(Context context, long mailboxId) {
//                return CurrentUserSettings.getSettings().isEncrypted();
//            }
//
//            @Override
//            public void setEnabled(Context context, long mailboxId, boolean enabled) {
//                if (!GeneralSettings.getSettings().shouldMigrationStart()) {
//                    GeneralSettings.getSettings().setMigrationShouldStart(true);
//                    CurrentUserSettings.getSettings(context).saveFlurryData();
//                }
//            }
//        });
//
//        sKnownFeatures.put("BlockedMessageForwarding", new FeatureDBHandle() {
//            @Override
//            public boolean isEnabled(Context context, long mailboxId) {
//                return CurrentUserSettings.getSettings(context).getHideOpenEncryptedFiles();
//            }
//
//            @Override
//            public void setEnabled(Context context, long mailboxId, boolean enabled) {
//                CurrentUserSettings.getSettings(context).setHideOpenEncryptedFiles(enabled);
//            }
//        });
//
//        sKnownFeatures.put("HDVoice", new FeatureDBHandle() {
//            @Override
//            public boolean isEnabled(Context context, long mailboxId) {
//                return RCMProviderHelper.isHDVoiceFeatureEnabled(context, mailboxId);
//            }
//
//            @Override
//            public void setEnabled(Context context, long mailboxId, boolean enabled) {
//                RCMProviderHelper.setHDVoiceFeatureEnabled(context, mailboxId, enabled);
//            }
//        });
//
//        sKnownFeatures.put("VoicemailToText", new FeatureDBHandle() {
//            @Override
//            public boolean isEnabled(Context context, long mailboxId) {
//                return CurrentUserSettings.getSettings(context).isVmtFeatureEnabled();
//            }
//
//            @Override
//            public void setEnabled(Context context, long mailboxId, boolean enabled) {
//                CurrentUserSettings.getSettings(context).setVmtFeatureEnabled(enabled);
//                if (enabled) {
//                    MessageUtils.syncVmtStatus(context, mailboxId);
//                }
//            }
//        });
    }

}
