package com.example.nickgao.service.i18n;

import android.content.Context;

import com.example.nickgao.R;
import com.example.nickgao.datastore.LanguageDataStore;
import com.example.nickgao.service.model.extensioninfo.ExtensionLanguage;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by nick.gao on 2/1/17.
 */

public class Language {

    //int mName;
    Locale mLocale;
    boolean mDefLanguage;
    //public static final int INVALID_NAME = -1;
    public static final int INVALID_ID = -1;

    private static HashMap<String, Integer> nameMap = new HashMap<String, Integer>();

    static {
        nameMap.put("fr_CA", R.string.language_fr_CA);
        nameMap.put("en_GB", R.string.language_en_GB);
        nameMap.put("en_US", R.string.language_en_US);
        nameMap.put("fr_FR", R.string.language_fr_FR);
        nameMap.put("es_ES", R.string.language_es_ES);
        nameMap.put("es_MX", R.string.language_es_MX);
        nameMap.put("pt", R.string.language_pt);
        nameMap.put("pt_PT", R.string.language_pt_PT);
        nameMap.put("pt_BR", R.string.language_pt_BR);
        nameMap.put("bg", R.string.language_bg);
        nameMap.put("ca", R.string.language_ca);
        nameMap.put("be", R.string.language_be);
        nameMap.put("cs", R.string.language_cs);
        nameMap.put("de", R.string.language_de);
        nameMap.put("da", R.string.language_da);
        nameMap.put("el", R.string.language_el);
        nameMap.put("et", R.string.language_et);
        nameMap.put("it", R.string.language_it);
        nameMap.put("it_CH", R.string.language_it_CH);
        nameMap.put("is", R.string.language_is);
        nameMap.put("lv", R.string.language_lv);
        nameMap.put("pl", R.string.language_pl);
        nameMap.put("ro", R.string.language_ro);
        nameMap.put("sk", R.string.language_sk);
        nameMap.put("sl", R.string.language_sl);
        nameMap.put("sr", R.string.language_sr);
        nameMap.put("sv", R.string.language_sv);
        nameMap.put("fi", R.string.language_fi);
        nameMap.put("ga", R.string.language_ga);
        nameMap.put("hr", R.string.language_hr);
        nameMap.put("hu", R.string.language_hu);
        nameMap.put("ru", R.string.language_ru);
        nameMap.put("tr", R.string.language_tr);
        nameMap.put("uk", R.string.language_uk);
        nameMap.put("ar", R.string.language_ar);
        nameMap.put("ja", R.string.language_ja);
        nameMap.put("ko", R.string.language_ko);
        nameMap.put("nl", R.string.language_nl);
        nameMap.put("zh_CN", R.string.language_zh_CN);
        nameMap.put("zh_HK", R.string.language_zh_HK);
        nameMap.put("zh_TW", R.string.language_zh_TW);
    }

    public Language(String langCode, String countryCode, boolean def) {
        //String desc = String.format("%s_%s", langCode, countryCode);
        //mName = generateName(desc);
        mLocale = new Locale(langCode, countryCode);
        mDefLanguage = def;
    }

    public Language(String isoLocale, boolean def) {
        //String value = isoLocale.replaceFirst("-", "_");
        //mName = generateName(value);
        mLocale = generalLocal(isoLocale);
        mDefLanguage = def;
    }

    public static Locale generalLocal(String isoLocale) {
        if (isoLocale == null) {
            return null;
        }
        if (isoLocale.contains("-")) {
            isoLocale = isoLocale.replace("-", "_");
        }

        Locale locale = null;
        if (isoLocale.contains("_")) {
            String values[] = isoLocale.split("_");
            if (values != null && values.length > 0) {
                if (values.length == 1) {
                    locale = new Locale(values[0]);
                } else if (values.length == 2) {
                    locale = new Locale(values[0], values[1]);
                }
            }
        } else {
            locale = new Locale(isoLocale);
        }

        return locale;
    }

    public Language(int name, Locale locale, boolean def) {
        //mName = name;
        mLocale = locale;
        mDefLanguage = def;
    }

    /*
    public String getDefaultName(Context context) {
        return mName != INVALID_NAME ? context.getString(mName) : null;
    }

    public int getName() {
        return mName;
    }

    public void setName(int name) {
        this.mName = mName;
    }

    private int generateName(String isoLocale) {
        Integer value = nameMap.get(isoLocale);
        return (value != null) ? value : INVALID_NAME;
    }
    */

    public Locale getLocale() {
        return mLocale;
    }

    public boolean isDefLanguage() {
        return mDefLanguage;
    }

    public static Language getLanguage(String desc, boolean def) {
        return new Language(desc, def);
    }

    public static boolean isValidate(Language lang) {
        return (lang == null || lang.getLocale() == null) ? false : true;
    }

    public String getIsoLocale() {
        if (mLocale != null) {
            return mLocale.toString();
        }
        return null;
    }

    public String getFormatLocale() {
        String result = getIsoLocale();
        if (result != null) {
            if (result.contains("_")) {
                result = result.replace("_", "-");
            }
        }
        return result;
    }

    public String getDisplayName(Context context) {
        if (mLocale == null) {
            return null;
        }

        String name = null;
        do {
            Integer value = nameMap.get(getIsoLocale());
            if (value != null) {
                name = context.getString(value);
                break;
            }

            String languageCode = mLocale.getLanguage();
            if (languageCode == null || languageCode.trim().isEmpty()) {
                break;
            }

            for (String key : nameMap.keySet()) {
                if (key != null && key.startsWith(languageCode)) {
                    value = nameMap.get(key);
                    if (value != null) {
                        name = context.getString(value);
                        break;
                    }
                }
            }
            //has found
            if (name != null) {
                break;
            }

            //try to use extension table to query language name
            ExtensionLanguage extensionSerLang = LanguageDataStore.getExtensionUserServerLanguage();
            if (extensionSerLang == null) {
                break;
            }
            name = extensionSerLang.getName();
        } while (false);

        //finally we also need to make sure the name should be a value.
        if (name == null || name.trim().isEmpty()) {
            //try to use language list map
            name = LanguageDataStore.getLanguageName(getFormatLocale());
            //make sure use language code if name = null or empty.
            if (name == null || name.trim().isEmpty()) {
                name = mLocale.toString();
            }
        }

        return name;
    }

    /*
    public String getNativeName(Context context) {
        String nativeName = LanguageDataStore.getLanguageName(getFormatLocale());
        if (nativeName == null) {
            nativeName = getDefaultName(context);
        }
        return nativeName;
    }*/

    public int getLanguageId() {
        int result = Language.INVALID_ID;
        String formatLocale = getFormatLocale();
        if (formatLocale != null) {
            result = LanguageDataStore.getLanguageId(formatLocale);
        }
        return result;
    }


    @Override
    public boolean equals(Object object) {
        boolean result = false;
        do {
            if (object == this) {
                result = true;
                break;
            }

            if (!(object instanceof Language)) {
                break;
            }

            Language o = (Language) object;
            if (o.getLocale() == null) {
                result = (this.getLocale() != null) ? false : true;
                break;
            }

            result = o.getLocale().equals(this.getLocale());
        } while (false);

        return result;
    }

}
