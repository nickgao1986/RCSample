package com.example.nickgao.database;

import java.net.URLEncoder;

import com.example.nickgao.logging.EngLog;

import android.net.Uri;
import android.net.Uri.Builder;


public class UriHelper {

    private static String TAG = "[RC] UriHelper";
    private static final Uri URI_MAILBOX_CURRENT = Uri.parse(  "content://" 
                                                           + RCMProvider.AUTHORITY + '/'
                                                           + RCMProvider.MAILBOX_CURRENT);

    public static Uri getUriMailboxCurrent()
    {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getUriMailboxCurrent()");

        return URI_MAILBOX_CURRENT;
    }
    
    public static Uri getUri(String path) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getUri(" + path + ')');
        
        Uri uri = prepare(path).build();
        
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "uri: " + uri);

        return uri;
    }
    
    public static Uri getSettingsUri(final String path) {
        return new Builder().scheme("content")
                .authority(RCMSettingsProvider.AUTHORITY)
                .path(path)
                .query("")
                .fragment("")
                .appendPath("")
                .build();
    }

    
    
//    public static Uri getSearchUri(String path){
//		Uri uri = new Uri.Builder().scheme("content")
//				.authority(SearchProvider.AUTHORITY)
//				.path(path).query("")
//				.fragment("")
//				.build();
//    	
//    	return uri;
//    }
//    
//    public static Uri getMessageUri(String path) {
//        return new Uri.Builder().scheme("content")
//                                .authority(RCMMessageProvider.AUTHORITY)
//                                .path(path)
//                                .query("")                                   
//                                .fragment("")
//                                .build();  
//    }
//    
//    public static Uri getMessageUri(String path, long item_id) {
//        return new Uri.Builder().scheme("content")
//                                .authority(RCMMessageProvider.AUTHORITY)
//                                .path(path)
//                                .query("")                                   
//                                .fragment("")
//                                .appendPath(String.valueOf(item_id))
//                                .build();  
//    }
//    
//    public static Uri getMessageUri(String path, String pathSegment) {
//        return new Uri.Builder().scheme("content")
//                                .authority(RCMMessageProvider.AUTHORITY)
//                                .path(path)
//                                .query("")                                   
//                                .fragment("")
//                                .appendPath(URLEncoder.encode(pathSegment))
//                                .build();  
//    }
    
    public static Uri getUri(String path, long mailbox_id) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getUri(" + path + ", " + mailbox_id + ')');
        
        Uri uri = prepare(path, mailbox_id).build();
        
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "uri: " + uri);

        return uri;
    }
    
    public static Uri getTextMessageUri(String path, long item_id) {
    	 return new Builder().scheme("content")
                 .authority(RCMProvider.AUTHORITY)
                 .path(path)
                 .query("")                                   
                 .fragment("")
                 .appendPath(String.valueOf(item_id))
                 .build();  
    }
    
    public static Uri getUri(String path, long mailbox_id, long item_id) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getUri(" + path + ", " + mailbox_id + ", " + item_id + ')');
        
        Uri uri = prepare(path, mailbox_id).appendPath(String.valueOf(item_id)).build();

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "uri: " + uri);

        return uri;
    }
    
    static Uri removeQuery(Uri uri) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "removeQuery(" + uri + ")");

        Uri newUri = uri.buildUpon().query("").build();

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "new uri: " + newUri);
        
        return newUri;
    }
    
    private static Builder prepare(String path) {
        return new Builder().scheme("content")
                                .authority(RCMProvider.AUTHORITY)
                                .path(path)
                                .query("")      // This is a workaround for Android 1.5 bug:                                           
                                .fragment("");  // NullPointerException at android.net.Uri$HierarchicalUri.writeToParcel(Uri.java:1117,1118)
                                                // (called during ContentProvider.notifyChange() execution)                            
                                                
    }
    
    private static Builder prepare(String path, long mailbox_id) {
    	Builder builder = prepare(path);
    	if(mailbox_id >= 0) {
    		builder.appendQueryParameter(RCMDataStore.RCMColumns.MAILBOX_ID, String.valueOf(mailbox_id));
    	}
    	return builder;
    }
}
