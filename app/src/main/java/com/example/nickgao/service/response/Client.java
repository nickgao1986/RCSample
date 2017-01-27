package com.example.nickgao.service.response;



/**
 * Created by nick.gao on 2014/7/16.
 */
public class Client extends AbstractModel {
    boolean detected;
    String userAgent;
    String appId;
    String appName;
    String appVersion;
    String appPlatform;
    String appPlatformVersion;
    String locale;

    public String getAppPlatform() {
        return appPlatform;
    }

    public void setAppPlatform(String appPlatform) {
        this.appPlatform = appPlatform;
    }

    public String getAppPlatformVersion() {
        return appPlatformVersion;
    }

    public void setAppPlatformVersion(String appPlatformVersion) {
        this.appPlatformVersion = appPlatformVersion;
    }

    public boolean isDetected() {
        return detected;
    }

    public void setDetected(boolean detected) {
        this.detected = detected;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
