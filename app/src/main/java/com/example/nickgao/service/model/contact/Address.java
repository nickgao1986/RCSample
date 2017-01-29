package com.example.nickgao.service.model.contact;

import android.text.TextUtils;

import com.example.nickgao.service.model.AbstractModel;


/**
 * Created by steve.chen on 6/30/14.
 */

public class Address extends AbstractModel {

    private String country;
    private String state;
    private String city;
    private String street;
    private String zip;

    public Address(String country, String state, String city, String street, String zip) {
        this.country = country;
        this.state = state;
        this.city = city;
        this.street = street;
        this.zip = zip;
    }

    public Address() {
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((street == null) ? 0 : street.hashCode());
        result = prime * result + ((zip == null) ? 0 : zip.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof Address)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        Address address = (Address) object;

        return TextUtils.equals(country, address.getCountry())
                && TextUtils.equals(state, address.getState())
                && TextUtils.equals(city, address.getCity())
                && TextUtils.equals(street, address.getStreet())
                && TextUtils.equals(zip, address.getZip());
    }

    public String value() {
        return value(false);
    }

    public String value(boolean countryIgnored) {
        StringBuilder builder = new StringBuilder();
        final String line = "\n";
        boolean hasStreet = false;
        boolean hasCity = false;
        boolean hasState = false;
        boolean hasZip = false;
        if(!TextUtils.isEmpty(street)) {
            builder.append(street);
            hasStreet = true;
        }


        if(!TextUtils.isEmpty(city)) {
            if(hasStreet) {
                builder.append(line);
            }
            builder.append(city);
            hasCity = true;
        }


        if(!TextUtils.isEmpty(state)) {

            if(hasCity) {
                builder.append(",");
            }else if(hasStreet) {
                builder.append(line);
            }

            builder.append(state);

            hasState = true;
        }

        if(!TextUtils.isEmpty(zip)) {
            if(hasState || hasCity) {
                builder.append(" ");
            }else if (hasStreet){
                builder.append(line);
            }
            builder.append(zip);
            hasZip = true;
        }

        if(!countryIgnored && !TextUtils.isEmpty(country)) {
            if(hasCity || hasState || hasZip || hasStreet) {
                builder.append(line);
            }

            builder.append(country);
        }

        return builder.toString();
    }

}
