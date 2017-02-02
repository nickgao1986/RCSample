package com.example.nickgao.service.model.extensioninfo;

/**
 * Created by jerry.cai on 9/9/2014.
 */
public class RegionalSettings {
    HomeCountry homeCountry;
    ExtensionLanguage language;
    ExtensionLanguage greetingLanguage;
    ExtensionLanguage formattingLocale;

    public HomeCountry getHomeCountry() {
        return homeCountry;
    }

    public void setHomeCountry(HomeCountry homeCountry) {
        this.homeCountry = homeCountry;
    }


    public ExtensionLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ExtensionLanguage language) {
        this.language = language;
    }

    public ExtensionLanguage getGreetingLanguage() {
        return greetingLanguage;
    }

    public void setGreetingLanguage(ExtensionLanguage greetingLanguage) {
        this.greetingLanguage = greetingLanguage;
    }

    public ExtensionLanguage getFormattingLocale() {
        return formattingLocale;
    }

    public void setFormattingLocale(ExtensionLanguage formattingLocale) {
        this.formattingLocale = formattingLocale;
    }
}
