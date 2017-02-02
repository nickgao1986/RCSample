package com.example.nickgao.service.model.extensioninfo;

import com.example.nickgao.service.model.contact.Contact;
import com.example.nickgao.service.response.AbstractResponse;

import java.security.Permissions;

/**
 * Created by nick.gao on 2/1/17.
 */

public class ExtensionInfoResponse extends AbstractResponse {
    String id;
    String setupWizardState;
    Contact contact;
    String extensionNumber;
    String name;
    String type;
    String status;
    Departments[] departments;
    ServiceFeatures[] serviceFeatures;
    RegionalSettings regionalSettings;
    Permissions permissions;

    public RegionalSettings getRegionalSettings() {
        return regionalSettings;
    }

    public void setRegionalSettings(RegionalSettings regionalSettings) {
        this.regionalSettings = regionalSettings;
    }



    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSetupWizardState() {
        return setupWizardState;
    }

    public void setSetupWizardState(String setupWizardState) {
        this.setupWizardState = setupWizardState;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Departments[] getDepartments() {
        return departments;
    }

    public void setDepartments(Departments[] departments) {
        this.departments = departments;
    }

    public ServiceFeatures[] getServiceFeatures() {
        return serviceFeatures;
    }

    public void setServiceFeatures(ServiceFeatures[] serviceFeatures) {
        this.serviceFeatures = serviceFeatures;
    }


}
