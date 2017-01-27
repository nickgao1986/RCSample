package com.example.nickgao.utils;

/** 
 * Copyright (C) 2012, RingCentral, Inc. 
 * All Rights Reserved.
 */

import android.text.TextUtils;

public class NumericVersion implements Comparable<NumericVersion> {
    private int major;
    private int minor;
    private int micro;
    private int revision;
    public static final NumericVersion ZERO = new NumericVersion(0, 0, 0, 0);
    
    private NumericVersion() {
    }
    
    public NumericVersion(int major, int minor, int micro, int revision) {
        this.major    = major;
        this.minor    = minor;
        this.micro    = micro;
        this.revision = revision;
        validate();
    }

    public int getMajor() {
        return major;
    }
    
    public int getMinor() {
        return minor;
    }
    
    public int getMicro() {
        return micro;
    }
    
    public int getRevision() {
        return revision;
    }
    
    public boolean isZero() {
        return equals(ZERO);
    }
    
    private void validate() {
        major    = major    < 0 ? 0 : major;
        minor    = minor    < 0 ? 0 : minor;
        micro    = micro    < 0 ? 0 : micro;
        revision = revision < 0 ? 0 : revision;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof NumericVersion) {
            return compareTo((NumericVersion) o) == 0;
        }
        return false;
    }
    
    @Override
    public int compareTo(NumericVersion to) {
        int result = Integer.valueOf(major).compareTo(Integer.valueOf(to.major));
        if (result != 0) {
            return result;
        }
        result = Integer.valueOf(minor).compareTo(Integer.valueOf(to.minor));
        if (result != 0) {
            return result;
        }
        result = Integer.valueOf(micro).compareTo(Integer.valueOf(to.micro));
        if (result != 0) {
            return result;
        }
        result = Integer.valueOf(revision).compareTo(Integer.valueOf(to.revision));
        if (result != 0) {
            return result;
        }
        return 0;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer().append("Version: ").append(getVersion());
        return sb.toString();
    }
    
    public String getVersion() {
        StringBuffer sb = new StringBuffer()
            .append(major).append('.')
            .append(minor).append('.')
            .append(micro).append('.')
            .append(revision);
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        int result = 33284;
        result ^= major;
        result ^= minor << 1;
        result ^= micro << 2;
        result ^= revision << 3;
        return result;
    }
    
    public static NumericVersion parse(String version) {
        if (version == null || TextUtils.isEmpty(version.trim())) {
            return ZERO;
        }
        try {
            NumericVersion nVersion = new NumericVersion();
            String[] values = version.split("\\.");
            if (values != null && values.length > 0) {
                int len = values.length > 4 ? 4 : values.length;
                for (int i = 0; i < len; i++) {
                    switch (i) {
                    case 0:
                        nVersion.major = getIntValue(values[i]);
                        break;
                    case 1:
                        nVersion.minor = getIntValue(values[i]);
                        break;
                    case 2:
                        nVersion.micro = getIntValue(values[i]);
                        break;
                    case 3:
                        nVersion.revision = getIntValue(values[i]);
                        break;
                    }
                }
            }
            nVersion.validate();
            return nVersion;
        } catch (Throwable th) {

        }
        return ZERO;
    }
    
    private static int getIntValue(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Throwable th) {
        }
        return 0;
    }
}
