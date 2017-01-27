/**
 * Copyright (C) 2013, RingCentral, Inc.
 * All Rights Reserved.
 */
package com.example.nickgao.network;

import android.content.ContentValues;

import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.database.RCMProviderHelper;
import com.example.nickgao.rcproject.RingCentralApp;
import com.google.agson.stream.JsonReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RestSyncInfo {

    private static final String TAG = "[RC]SyncInfo";

    public String syncType = "";
    public String syncToken = "";
    public String syncTime = "";


    public RestSyncInfo parse(JsonReader reader) throws IOException {
        reader.beginObject();

        String key;
        while (reader.hasNext()) {
            key = reader.nextName();

            if (key.equals(RestVocabulary.SyncInfoKey.SYNC_TYPE)) {
                syncType = reader.nextString();
            } else if (key.equals(RestVocabulary.SyncInfoKey.SYNC_TOKEN)) {
                syncToken = reader.nextString();
            } else if (key.equals(RestVocabulary.SyncInfoKey.SYNC_TIME)) {
                syncTime = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return this;
    }

    public ContentValues fillContentValues() {
        ContentValues values = new ContentValues();

        values.put(RCMDataStore.RCMColumns.MAILBOX_ID, 				RCMProviderHelper.getCurrentMailboxId(RingCentralApp.getContextRC()));
        values.put(RCMDataStore.MessageListTable.REST_SYNC_TOKEN, 	syncToken);

        long syncTimeMillisec = iso8601TimeToMilliseconds(syncTime);
        if (syncTimeMillisec != 0) {
            values.put(RCMDataStore.MessageListTable.REST_SYNC_TIME, syncTimeMillisec);
        }

        return values;
    }
    
    
    public  long iso8601TimeToMilliseconds(String iso8601Time) {

        Date date;
        try {
        	TimeZone tz = TimeZone.getTimeZone("UTC");
        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        	df.setTimeZone(tz);
            date = df.parse(iso8601Time);
        } catch (Throwable e) {
            return 0;
        }

        long result = date.getTime();
        return result;
    }
}