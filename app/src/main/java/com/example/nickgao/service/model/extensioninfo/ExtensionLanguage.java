package com.example.nickgao.service.model.extensioninfo;

/**
 * Created by jerry.cai on 9/9/2014.
 */
public class ExtensionLanguage {
    int id;
    String name;
    String localeCode;

    public ExtensionLanguage(int id, String name, String localeCode) {
        this.id = id;
        this.name = name;
        this.localeCode = localeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isValid() {
        return !(this.name == null || this.name.isEmpty()
                || this.localeCode == null || this.localeCode.isEmpty());
    }
}
