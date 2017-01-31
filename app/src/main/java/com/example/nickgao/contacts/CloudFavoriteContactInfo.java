package com.example.nickgao.contacts;

/**
 * Created by nick.gao on 1/31/17.
 */

public class CloudFavoriteContactInfo {

    public static class Builder {
        public static CloudFavoriteContactInfo extensionFavorite(int id, String extensionId) {
            CloudFavoriteContactInfo info = new CloudFavoriteContactInfo();
            info.id = id;
            info.extensionId = extensionId;
            return info;
        }

        public static CloudFavoriteContactInfo cloudContactFavorite(int id, String contactId) {
            CloudFavoriteContactInfo info = new CloudFavoriteContactInfo();
            info.id = id;
            info.contactId = contactId;
            return info;
        }
    }

    public int id;
    public String extensionId;
    public String contactId;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id:").append(id);
        sb.append("extension:").append(extensionId);
        sb.append("contactId:").append(contactId);
        return sb.toString();
    }


}
