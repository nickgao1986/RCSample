package com.example.nickgao.service.model.contact;

/**
 * Created by coa.ke on 2014/7/4.
 */
public class PermissionsList {
    private Permission admin;
    private Permission internationalCalling;

    public Permission getAdmin() {
        return admin;
    }

    public void setAdmin(Permission admin) {
        this.admin = admin;
    }

    public Permission getInternationalCalling() {
        return internationalCalling;
    }

    public void setInternationalCalling(Permission internationalCalling) {
        this.internationalCalling = internationalCalling;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((admin == null) ? 0 : admin.hashCode());
        result = prime * result + ((internationalCalling == null) ? 0 : internationalCalling.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof PermissionsList)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        PermissionsList permissionsList = (PermissionsList) object;
        if (admin == null) {
            admin = new Permission();
        }

        if (internationalCalling == null) {
            internationalCalling = new Permission();
        }

        if (permissionsList.getAdmin() == null) {
            permissionsList.setAdmin(new Permission());
        }

        if (permissionsList.getInternationalCalling() == null) {
            permissionsList.setInternationalCalling(new Permission());
        }

        return admin.equals(permissionsList.getAdmin())
                && internationalCalling.equals(permissionsList.getInternationalCalling());
    }
}
