package com.example.nickgao.contacts.adapters.contactsprovider;

import android.text.TextUtils;

import com.example.nickgao.service.model.contact.Address;
import com.example.nickgao.utils.DateUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick.gao on 1/30/17.
 */

public class CloudPersonalContactInfo {

    public long id;
    public String uri;
    public String availability;
    public String firstName;
    public String lastName;
    public String middleName;
    public String nickName;
    public String company;
    public String jobTitle;
    public String homePhone;
    public String homePhone2;
    public String businessPhone;
    public String businessPhone2;
    public String mobilePhone;
    public String businessFax;
    public String companyPhone;
    public String assistantPhone;
    public String carPhone;
    public String otherPhone;
    public String otherFax;
    public String callbackPhone;
    public String email;
    public String email2;
    public String email3;
    public Address homeAddress;
    public Address businessAddress;
    public Address otherAddress;
    public String birthday;
    public String webPage;
    public String notes;

    public CloudPersonalContactInfo() {
    }

    public CloudPersonalContactInfo(long id) {
        this.id = id;
    }

    public List<Contact.TypeValue> getPhoneNumbers() {
        //base on https://wiki.ringcentral.com/display/PM/Mobile+Cloud+Contact+Field+Mapping+Rules
        List<Contact.TypeValue> ret = new ArrayList<>();
        addToList(ret, CloudPersonalContact.PhoneType.HOME_PHONE.ordinal(), this.homePhone);
        addToList(ret, CloudPersonalContact.PhoneType.HOME_PHONE2.ordinal(), this.homePhone2);
        addToList(ret, CloudPersonalContact.PhoneType.BUSINESS_PHONE.ordinal(), this.businessPhone);
        addToList(ret, CloudPersonalContact.PhoneType.BUSINESS_PHONE2.ordinal(), this.businessPhone2);
        addToList(ret, CloudPersonalContact.PhoneType.MOBILE_PHONE.ordinal(), this.mobilePhone);
        addToList(ret, CloudPersonalContact.PhoneType.BUSINESS_FAX.ordinal(), this.businessFax);
        addToList(ret, CloudPersonalContact.PhoneType.COMPANY_PHONE.ordinal(), this.companyPhone);
        addToList(ret, CloudPersonalContact.PhoneType.ASSISTANT_PHONE.ordinal(), this.assistantPhone);
        addToList(ret, CloudPersonalContact.PhoneType.CAR_PHONE.ordinal(), this.carPhone);
        addToList(ret, CloudPersonalContact.PhoneType.OTHER_PHONE.ordinal(), this.otherPhone);
        addToList(ret, CloudPersonalContact.PhoneType.OTHER_FAX.ordinal(), this.otherFax);
        addToList(ret, CloudPersonalContact.PhoneType.CALLBACK_PHONE.ordinal(), this.callbackPhone);
        return ret;
    }

    public List<Contact.TypeValue> getEmailAddressList() {
        List<Contact.TypeValue> ret = new ArrayList<>();
        addToList(ret, CloudPersonalContact.EmailType.EMAIL.ordinal(), this.email);
        addToList(ret, CloudPersonalContact.EmailType.EMAIL2.ordinal(), this.email2);
        addToList(ret, CloudPersonalContact.EmailType.EMAIL3.ordinal(), this.email3);
        return ret;
    }

    public List<Contact.TypeAddress> getAddressList() {
        List<Contact.TypeAddress> ret = new ArrayList<>();
        if (this.homeAddress != null && !TextUtils.isEmpty(this.homeAddress.value(true))) {
            ret.add(new Contact.TypeAddress(CloudPersonalContact.AddressType.HOME_ADDRESS.ordinal(), this.homeAddress));
        }

        if (this.businessAddress != null && !TextUtils.isEmpty(this.businessAddress.value(true))) {
            ret.add(new Contact.TypeAddress(CloudPersonalContact.AddressType.BUSINESS_ADDRESS.ordinal(), this.businessAddress));
        }

        if (this.otherAddress != null && !TextUtils.isEmpty(this.otherAddress.value(true))) {
            ret.add(new Contact.TypeAddress(CloudPersonalContact.AddressType.OTHER_ADDRESS.ordinal(), this.otherAddress));
        }
        return ret;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (!Contact.isEmpty(firstName)) {
            sb.append(firstName.trim());
        }
        if (!Contact.isEmpty(middleName)) {
            sb.append(" ").append(middleName.trim());
        }

        if (!Contact.isEmpty(lastName)) {
            sb.append(" ").append(lastName.trim());
        }
        return sb.toString().trim();
    }

    public String getDisplayName() {
        String name = getFullName();

        do {
            if (!Contact.isEmpty(name)) {
                break;
            }

            //nick name
            if (!Contact.isEmpty(nickName)) {
                name = nickName.trim();
                break;
            }

            //company
            if (!Contact.isEmpty(company)) {
                name = company.trim();
                break;
            }

            //mail address
            Contact.TypeValue typeValue;
            List<Contact.TypeValue> listMail = getEmailAddressList();
            if (!listMail.isEmpty()) {
                for (int i = 0; i < listMail.size(); i++) {
                    typeValue = listMail.get(i);
                    name = typeValue.getValue().trim();
                    if (!Contact.isEmpty(name)) {
                        break;
                    }
                }
            }

            if (!Contact.isEmpty(name)) {
                break;
            }

            //phone number
            List<Contact.TypeValue> listPhone = getPhoneNumbers();
            if (!listPhone.isEmpty()) {
                for (int i = 0; i < listPhone.size(); i++) {
                    typeValue = listPhone.get(i);
                    name = typeValue.getValue().trim();
                    if (!Contact.isEmpty(name)) {
                        break;
                    }
                }
            }

            if (!Contact.isEmpty(name)) {
                break;
            }

            name = Contact.NO_MANE;
        } while (false);

        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PersonalContactInfo ")
                .append(" id:").append(id)
                .append(" uri:").append(uri)
                .append(" availability:").append(availability)
                .append(" display name: ").append(getDisplayName())
                .append(" firstName:").append(firstName)
                .append(" lastName:").append(lastName)
                .append(" middleName:").append(middleName)
                .append(" nickName:").append(nickName)
                .append(" company:").append(company)
                .append(" jobTitle:").append(jobTitle)
                .append(" homePhone:").append(homePhone)
                .append(" homePhone2:").append(homePhone2)
                .append(" businessPhone:").append(businessPhone)
                .append(" businessPhone2:").append(businessPhone2)
                .append(" mobilePhone:").append(mobilePhone)
                .append(" businessFax:").append(businessFax)
                .append(" companyPhone:").append(companyPhone)
                .append(" assistantPhone:").append(assistantPhone)
                .append(" carPhone:").append(carPhone)
                .append(" otherPhone:").append(otherPhone)
                .append(" otherFax:").append(otherFax)
                .append(" callbackPhone:").append(callbackPhone)
                .append(" email:").append(email)
                .append(" email2:").append(email2)
                .append(" email3:").append(email3)
                .append(" homeAddress:").append(homeAddress)
                .append(" businessAddress:").append(businessAddress)
                .append(" otherAddress:").append(otherAddress)
                .append(" birthday:").append(birthday)
                .append(" webPage:").append(webPage)
                .append(" notes:").append(notes);

        return sb.toString();
    }

    public String toJson() {
        Gson gson = new Gson();
        if (id < 0) {
            CreateContactInfo info = new CreateContactInfo();
            info.firstName = this.firstName;
            info.lastName = this.lastName;
            info.middleName = this.middleName;
            info.nickName = this.nickName;
            info.company = this.company;
            info.jobTitle = this.jobTitle;
            info.homePhone = this.homePhone;
            info.homePhone2 = this.homePhone2;
            info.businessPhone = this.businessPhone;
            info.businessPhone2 = this.businessPhone2;
            info.mobilePhone = this.mobilePhone;
            info.businessFax = this.businessFax;
            info.companyPhone = this.companyPhone;
            info.assistantPhone = this.assistantPhone;
            info.carPhone = this.carPhone;
            info.otherPhone = this.otherPhone;
            info.otherFax = this.otherFax;
            info.callbackPhone = this.callbackPhone;
            info.email = this.email;
            info.email2 = this.email2;
            info.email3 = this.email3;
            info.homeAddress = this.homeAddress;
            info.businessAddress = this.businessAddress;
            info.otherAddress = this.otherAddress;
            info.birthday = TextUtils.isEmpty(this.birthday)? "" : DateUtils.getUTCFormatTime(this.birthday);
            info.webPage = this.webPage;
            info.notes = this.notes;
            return gson.toJson(info);
        }
        return gson.toJson(this);
    }

    private void addToList(List<Contact.TypeValue> container, int type, String data) {
        if (!TextUtils.isEmpty(data)) {
            data = data.trim();
            if (data.length() > 0) {
                container.add(new Contact.TypeValue(type, data));
            }
        }
    }

    private class CreateContactInfo {
        public String firstName;
        public String lastName;
        public String middleName;
        public String nickName;
        public String company;
        public String jobTitle;
        public String homePhone;
        public String homePhone2;
        public String businessPhone;
        public String businessPhone2;
        public String mobilePhone;
        public String businessFax;
        public String companyPhone;
        public String assistantPhone;
        public String carPhone;
        public String otherPhone;
        public String otherFax;
        public String callbackPhone;
        public String email;
        public String email2;
        public String email3;
        public Address homeAddress;
        public Address businessAddress;
        public Address otherAddress;
        public String birthday;
        public String webPage;
        public String notes;
    }

}
