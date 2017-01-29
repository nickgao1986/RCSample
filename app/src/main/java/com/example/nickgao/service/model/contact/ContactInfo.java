package com.example.nickgao.service.model.contact;

import android.text.TextUtils;

import com.example.nickgao.service.model.AbstractModel;

/**
 * Created by steve.chen on 6/30/14.
 */
public class ContactInfo extends AbstractModel {

    private String firstName;
    private String lastName;
    private String company;
    private String email;
    private String businessPhone;
    private Address businessAddress;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(Address businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((businessPhone == null) ? 0 : businessPhone.hashCode());
        result = prime * result + ((businessAddress == null) ? 0 : businessAddress.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof ContactInfo)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        ContactInfo contactInfo = (ContactInfo) object;

        if (businessAddress == null) {
            businessAddress = new Address();
        }

        if (contactInfo.getBusinessAddress() == null) {
            contactInfo.setBusinessAddress(new Address());
        }

        //                && TextUtils.equals(company, contactInfo.getCompany())
        return TextUtils.equals(firstName, contactInfo.getFirstName())
                && TextUtils.equals(lastName, contactInfo.getLastName())
//                && TextUtils.equals(company, contactInfo.getCompany())
                && TextUtils.equals(email, contactInfo.getEmail())
                && TextUtils.equals(businessPhone, contactInfo.getBusinessPhone())
                && businessAddress.equals(contactInfo.getBusinessAddress());
    }
}
