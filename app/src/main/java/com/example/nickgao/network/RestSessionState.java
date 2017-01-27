/** 
 * Copyright (C) 2012, RingCentral, Inc.
 *  
 * All Rights Reserved.
 */
package com.example.nickgao.network;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Defines  
 */
public enum RestSessionState implements Parcelable {
    /**
     * Initial state after {@link RESTSession#createSession(android.content.Context, long)}
     * 
     * Next states: {@link #AUTHORIZATION} and {@value #DESTROYED}
     */
    INITIAL, 
    
    /**
     * Defines the state when an authorization is in progress.
     * 
     * Next states: {@link #AUTHORIZED} and {@value #AUTHORIZATION_FAILED}
     */
    AUTHORIZATION,
    
    /**
     * Defines the sate when an authorization is (was successful)
     * 
     * Next states: {@link #AUTHORIZATION} on
     * {@link RESTSession#authorize(android.content.Context, String, String, String)} and {@value #DESTROYED}
     */
    AUTHORIZED,
    
    /**
     * Defines the sate when an authorization was unsuccessful) with error code (the reason of failure) 
     * available RESTSession 
     * 
     * Next states: {@link #AUTHORIZATION} and {@value #DESTROYED}
     */
    AUTHORIZATION_FAILED, 
    
    /**
     * Defines the sate when the session is nor valid any more.
     * 
     * Next states: not applicable
     */
    DESTROYED;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(ordinal());
    }

    /**
     * Parcelable CREATOR.
     */
    public static final Creator<RestSessionState> CREATOR = new Creator<RestSessionState>() {
        @Override
        public RestSessionState createFromParcel(final Parcel source) {
            return RestSessionState.values()[source.readInt()];
        }

        @Override
        public RestSessionState[] newArray(final int size) {
            return new RestSessionState[size];
        }
    };
}
