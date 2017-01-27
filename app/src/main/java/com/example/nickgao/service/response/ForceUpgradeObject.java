package com.example.nickgao.service.response;

public class ForceUpgradeObject {

    private String serverVersion;

    private String appDownloadUri;

    private boolean isForceUpgrade;

    private boolean isOptionalUpgrade;

    private String supportOs;

    public String getSupportOs() {
        return supportOs;
    }

    public void setSupportOs(String supportOs) {
        this.supportOs = supportOs;
    }

    public String getAppDownloadUri() {
        return appDownloadUri;
    }

    public void setAppDownloadUri(String appDownload) {
        this.appDownloadUri = appDownload;
    }


    public boolean isOptionalUpgrade() {
        return isOptionalUpgrade;
    }

    public void setOptionalUpgrade(boolean isOptionalUpgrade) {
        this.isOptionalUpgrade = isOptionalUpgrade;
    }

    public boolean isForceUpgrade() {
        return isForceUpgrade;
    }

    public void setForceUpgrade(boolean isForceUpgrade) {
        this.isForceUpgrade = isForceUpgrade;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

}
