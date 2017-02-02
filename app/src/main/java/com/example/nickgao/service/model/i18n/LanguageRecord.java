package com.example.nickgao.service.model.i18n;

/**
 * Created by nick.gao on 2/1/17.
 */

public class LanguageRecord {
    /*
     "uri" : ".../restapi/v1.0/dictionary/language/1033",
    "id" : "1033",
    "name" : "English (United States)",
    "isoCode" : "en",
    "localeCode": "en-US"
     */
    String uri;
    int id;
    String name;
    String isoCode;
    String localeCode;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof LanguageRecord)) {
            return false;
        }

        if (object == this) {
            return true;
        }
        LanguageRecord tmp = (LanguageRecord) object;

        return this.id == tmp.getId();
    }
}
