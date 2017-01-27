/** 
 * Copyright (C) 2012, RingCentral, Inc.
 *  
 * All Rights Reserved.
 */
package com.example.nickgao.network;

import android.os.Parcel;
import android.os.Parcelable;

public class RestSessionStateChange implements Parcelable {
    public final static String REST_SESSION_STATE_CHANGE_NOTIFICATION = 
        "com.rcbase.android.restapi.REST_SESSION_STATE_CHANGE_NOTIFICATION";
    public final static String REST_SESSION_STATE_CHANGE_TAG = 
        "com.rcbase.android.restapi.REST_SESSION_STATE_TAG";

    private long mMailBoxId;
    private long mFactoryId;
    private RestSessionState mState;
    private int  mStatusCode;

    RestSessionStateChange(long factoryId, long mailboxId, RestSessionState state, int statusCode) {
        mFactoryId = factoryId;
        mMailBoxId = mailboxId;
        mState = state;
        mStatusCode = statusCode;
    }

    public long getMailBoxId() {
        return mMailBoxId;
    }

    public long getFactoryId() {
        return mFactoryId;
    }

    public RestSessionState getState() {
        return mState;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeLong(mFactoryId);
        dest.writeLong(mMailBoxId);
        mState.writeToParcel(dest, flags);
        dest.writeInt(mStatusCode);
    }

    public static final Creator<RestSessionStateChange> CREATOR = new Creator<RestSessionStateChange>() {
        @Override
        public RestSessionStateChange createFromParcel(final Parcel source) {
            return new RestSessionStateChange(source.readLong(), source.readLong(),
                    RestSessionState.values()[source.readInt()], source.readInt());
        }

        @Override
        public RestSessionStateChange[] newArray(final int size) {
            return new RestSessionStateChange[size];
        }
    };
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("RestSessionStateChange: ")
        .append(";  factoryId:").append(mFactoryId)
        .append(";  mailboxId:").append(mMailBoxId)
        .append(";  state:").append(mState.name())
        .append(";  status:").append(RestApiErrorCodes.getMsg(mStatusCode));
        return sb.toString();
    }
}
