package com.example.nickgao.service.response;

/**
 * Created by nick.gao on 2014/7/16.
 */
public class WebUris {
    String expressSetupMobile;
    String mobileWebBilling;
    String mobileWebPhoneSystem;
    String mobileWebUserSettings;
    String mobileWebTellAFriend;
    String mobileWebInternationalCalling;
    String mobileWebCallHandling;
    String mobileWebNotifications;
    String mobileWebReporting;
    String serviceWebPhoneSystem;
    String serviceWebUserSettings;
    String serviceWebBilling;
    String serviceWebTellAFriend;
    String appDownload;
    long expiresIn;
    String mobileWebResetPassword;
    String mobileWebChangePassword;
    String mobileWebUsers;

    String mobileWebTrialUpgrade;

    String eula;
    String emergencyDisclaimer;

    String mobileAssetsHome;

    public String getMobileWebChangePassword() {
        return mobileWebChangePassword;
    }

    public void setMobileWebChangePassword(String mobileWebChangePassword) {
        this.mobileWebChangePassword = mobileWebChangePassword;
    }

    public String getMobileWebResetPassword() {
        return mobileWebResetPassword;
    }

    public void setMobileWebResetPassword(String mobileWebResetPassword) {
        this.mobileWebResetPassword = mobileWebResetPassword;
    }

    public String getExpressSetupMobile() {
        return expressSetupMobile;
    }

    public void setExpressSetupMobile(String expressSetupMobile) {
        this.expressSetupMobile = expressSetupMobile;
    }


    public String getMobileWebTrialUpgrade() {
        return mobileWebTrialUpgrade;
    }

    public void setMobileWebTrialUpgrade(String mobileWebTrialUpgrade) {
        this.mobileWebTrialUpgrade = mobileWebTrialUpgrade;
    }


    public String getMobileWebBilling() {
        return mobileWebBilling;
    }

    public void setMobileWebBilling(String mobileWebBilling) {
        this.mobileWebBilling = mobileWebBilling;
    }

    public String getMobileWebPhoneSystem() {
        return mobileWebPhoneSystem;
    }

    public void setMobileWebPhoneSystem(String mobileWebPhoneSystem) {
        this.mobileWebPhoneSystem = mobileWebPhoneSystem;
    }

    public String getMobileWebUserSettings() {
        return mobileWebUserSettings;
    }

    public void setMobileWebUserSettings(String mobileWebUserSettings) {
        this.mobileWebUserSettings = mobileWebUserSettings;
    }

    public String getMobileWebTellAFriend() {
        return mobileWebTellAFriend;
    }

    public void setMobileWebTellAFriend(String mobileWebTellAFriend) {
        this.mobileWebTellAFriend = mobileWebTellAFriend;
    }

    public String getMobileWebInternationalCalling() {
        return mobileWebInternationalCalling;
    }

    public void setMobileWebInternationalCalling(String mobileWebInternationalCalling) {
        this.mobileWebInternationalCalling = mobileWebInternationalCalling;
    }

    public String getMobileWebCallHandling() {
        return mobileWebCallHandling;
    }

    public void setMobileWebCallHandling(String mobileWebCallHandling) {
        this.mobileWebCallHandling = mobileWebCallHandling;
    }

    public String getMobileWebNotifications() {
        return mobileWebNotifications;
    }

    public void setMobileWebNotifications(String mobileWebNotifications) {
        this.mobileWebNotifications = mobileWebNotifications;
    }

    public String getMobileWebReporting() {
        return mobileWebReporting;
    }

    public void setMobileWebReporting(String mobileWebReporting) {
        this.mobileWebReporting = mobileWebReporting;
    }

    public String getServiceWebPhoneSystem() {
        return serviceWebPhoneSystem;
    }

    public void setServiceWebPhoneSystem(String serviceWebPhoneSystem) {
        this.serviceWebPhoneSystem = serviceWebPhoneSystem;
    }

    public String getServiceWebUserSettings() {
        return serviceWebUserSettings;
    }

    public void setServiceWebUserSettings(String serviceWebUserSettings) {
        this.serviceWebUserSettings = serviceWebUserSettings;
    }

    public String getServiceWebBilling() {
        return serviceWebBilling;
    }

    public void setServiceWebBilling(String serviceWebBilling) {
        this.serviceWebBilling = serviceWebBilling;
    }

    public String getServiceWebTellAFriend() {
        return serviceWebTellAFriend;
    }

    public void setServiceWebTellAFriend(String serviceWebTellAFriend) {
        this.serviceWebTellAFriend = serviceWebTellAFriend;
    }

    public String getAppDownload() {
        return appDownload;
    }

    public void setAppDownload(String appDownload) {
        this.appDownload = appDownload;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }


    public String getMobileWebUsers() {
        return mobileWebUsers;
    }

    public void setMobileWebUsers(String mobileWebUsers) {
        this.mobileWebUsers = mobileWebUsers;
    }

    public String getEULA() {
        return eula;
    }

    public String getEmergencyDisclaimer() {
        return emergencyDisclaimer;
    }

    public void setMobileAssetsHome(String mobileAssetsHome) { this.mobileAssetsHome = mobileAssetsHome; }

    public String getMobileAssetsHome() {
        if (mobileAssetsHome == null || mobileAssetsHome.isEmpty())
        {
            return "https://downloads.ringcentral.com";
        }

        return mobileAssetsHome;
    }
}
