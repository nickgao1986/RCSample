package com.example.nickgao.service.model.extensioninfo;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nick.gao on 2016/3/21.
 */
public class ProfileUri implements Parcelable {

    public ProfileUri() {}

    private ProfileUri(Parcel in) {
        uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
    }

    String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public static final Creator<ProfileUri> CREATOR = new Creator<ProfileUri>() {
        public ProfileUri createFromParcel(Parcel in) {
            return new ProfileUri(in);
        }

        public ProfileUri[] newArray(int size) {
            return new ProfileUri[size];
        }
    };
}
