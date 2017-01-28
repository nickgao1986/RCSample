package com.example.nickgao.service.model.extensioninfo;

/**
 * Created by nick.gao on 1/28/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Antonenko Viacheslav on 16/09/15.
 */
public final class ProfileImage implements Parcelable {

    private String uri = "";
    private String etag = "";
    private String lastModified = "";
    private String contentType = "";

    private ProfileUri[] scales;

    public ProfileImage() {
    }

    public ProfileImage(String uri, String etag, String lastModified, String contentType) {
        this.uri = uri;
        this.etag = etag;
        this.lastModified = lastModified;
        this.contentType = contentType;
    }

    public ProfileUri[] getScales() {
        return scales;
    }

    public void setScales(ProfileUri[] scales) {
        this.scales = scales;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        result = prime * result + ((etag == null) ? 0 : etag.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof ProfileImage)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        ProfileImage profileImage = (ProfileImage) object;

        return uri.equals(profileImage.getUri())
                && etag.equals(profileImage.getEtag());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeString(etag);
        dest.writeString(lastModified);
        dest.writeString(contentType);
        dest.writeTypedArray(scales, 0);
    }

    public static final Creator<ProfileImage> CREATOR = new Creator<ProfileImage>() {
        public ProfileImage createFromParcel(Parcel in) {
            return new ProfileImage(in);
        }

        public ProfileImage[] newArray(int size) {
            return new ProfileImage[size];
        }
    };

    private ProfileImage(Parcel in) {
        uri = in.readString();
        etag = in.readString();
        lastModified = in.readString();
        contentType = in.readString();
        scales = in.createTypedArray(ProfileUri.CREATOR);
    }
}
