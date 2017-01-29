package com.example.nickgao.service.model.contact;

import android.text.TextUtils;

/**
 * Created by coa.ke on 2014/7/4.
 */
public class Permission {
    private String enabled;

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof Permission)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        Permission permission = (Permission) object;

        return TextUtils.equals(enabled, permission.enabled);
    }
}
