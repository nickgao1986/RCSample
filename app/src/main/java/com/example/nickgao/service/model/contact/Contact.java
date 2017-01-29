package com.example.nickgao.service.model.contact;

import android.text.TextUtils;

import com.example.nickgao.service.model.AbstractModel;
import com.example.nickgao.service.model.extensioninfo.ProfileImage;

/**
 * Created by steve.chen on 6/30/14.
 */
public class Contact extends AbstractModel {

    private String id;
    private String extensionNumber;
    private String name;
    private String type;
    private ContactInfo contact;
    private String status;
    private PermissionsList permissions;
    private ProfileImage profileImage;
    private boolean isStarred;
    private String sort;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExtensionNumber() {
        return extensionNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ContactInfo getContact() {
        return contact;
    }

    public void setContact(ContactInfo contact) {
        this.contact = contact;
    }

    public PermissionsList getPermissions() {
        return permissions;
    }

    public void setPermissions(PermissionsList permissions) {
        this.permissions = permissions;
    }

    public ProfileImage getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((extensionNumber == null) ? 0 : extensionNumber.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((contact == null) ? 0 : contact.hashCode());
        result = prime * result + ((permissions == null) ? 0 : permissions.hashCode());
        result = prime * result + ((profileImage == null) ? 0 : profileImage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof Contact)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        Contact extInfo = (Contact) object;

        if (contact == null) {
            contact = new ContactInfo();
        }

        if (extInfo.getContact() == null) {
            extInfo.setContact(new ContactInfo());
        }

        if (permissions == null) {
            permissions = new PermissionsList();
        }

        if (extInfo.getPermissions() == null) {
            extInfo.setPermissions(new PermissionsList());
        }

        if (extInfo.getProfileImage() == null) {
            extInfo.setProfileImage(new ProfileImage());
        }


        return TextUtils.equals(id, extInfo.getId())
                && TextUtils.equals(extensionNumber, extInfo.getExtensionNumber())
                && TextUtils.equals(name, extInfo.getName())
                && TextUtils.equals(type, extInfo.getType())
                && TextUtils.equals(status, extInfo.getStatus())
                && TextUtils.equals(profileImage.getEtag(), extInfo.getProfileImage().getEtag())
                && TextUtils.equals(profileImage.getUri(), extInfo.getProfileImage().getUri())
                && contact.equals(extInfo.getContact())
                && permissions.equals(extInfo.getPermissions());
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean isStarred) {
        this.isStarred = isStarred;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
