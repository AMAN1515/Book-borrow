package com.example.ks.bookstore.Account;

/**
 * Created by pankaj kumar on 12-02-2018.
 */

class Address{
    private String streetNo;
    private String landmark;
    private String city;
    private String zipCode;
    private String state;
    private String country;

    Address(String streetNo, String landmark, String city, String zipCode, String state, String country) {
        this.streetNo = streetNo;
        this.landmark = landmark;
        this.city = city;
        this.zipCode = zipCode;
        this.state = state;
        this.country = country;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }
}
