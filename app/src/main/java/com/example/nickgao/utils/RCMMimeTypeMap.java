package com.example.nickgao.utils;

import java.util.HashMap;

/**
 * Created by nick.gao on 2/4/17.
 */

public class RCMMimeTypeMap {

    private static final RCMMimeTypeMap sMimeTypeMap = new RCMMimeTypeMap();

    private static final HashMap<String, String> mimeType2extensionMap = new HashMap<String, String>();

    private RCMMimeTypeMap() {
    }

    public static RCMMimeTypeMap getSingleton() {
        return sMimeTypeMap;
    }

    static {
//    	See http://jira.ringcentral.com/browse/AB-6569 for detail
//    	add("application/postscript", 										"eps");
//    	add("application/dxf", 												"dxf");

        add("application/x-mswrite", "wri");
        add("application/x-mspublisher", "pub");
        add("application/vnd.ms-word.document.macroenabled.12", "docm");
        add("application/vnd.ms-powerpoint.presentation.macroenabled.12", "pptm");
        add("application/vnd.ms-excel.sheet.binary.macroenabled.12", "xlsb");
        add("application/vnd.ms-excel.sheet.macroenabled.12", "xlsm");
        add("application/vnd.wordperfect", "wpd");
        add("image/targa", "tga");
        add("application/vnd.ms-works", "wps");
        add("application/rtf", "rtf");
        add("application/xml", "xml");
        add("image/x-pcx", "pcx");
        add("application/photoshop", "psd");

    }

    public String getExtensionFromMimeType(String mimeType) {
        if (mimeType2extensionMap == null || mimeType2extensionMap.isEmpty()
                || !mimeType2extensionMap.containsKey(mimeType)) {
            return "";
        }
        return mimeType2extensionMap.get(mimeType);
    }

    private static void add(String mimeType, String extension) {
        mimeType2extensionMap.put(mimeType, extension);
    }


}
