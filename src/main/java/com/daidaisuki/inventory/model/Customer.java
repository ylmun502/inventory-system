package com.daidaisuki.inventory.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {
    private int id;
    private final StringProperty name;
    private final StringProperty phoneNumber;
    private final StringProperty email;
    private final StringProperty address;
    private final StringProperty platform;

    public Customer() {
        this.id = -1;
        this.name = new SimpleStringProperty("");
        this.phoneNumber = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.address = new SimpleStringProperty("");
        this.platform = new SimpleStringProperty("");
    }

    public Customer(int id, String name, String phoneNumber, String email, String address, String platform) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.email = new SimpleStringProperty(email);
        this.address = new SimpleStringProperty(address);
        this.platform = new SimpleStringProperty(platform);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name.get();
    }

    public void setName(String value) {
        this.name.set(value);
    }

    public StringProperty nameProperty() {
        return this.name;
    }

    public String getPhoneNumber() {
        return this.phoneNumber.get();
    }

    public void setPhoneNumber(String value) {
        this.phoneNumber.set(value);
    }

    public StringProperty phoneNumberProperty() {
        return this.phoneNumber;
    }

    public String getEmail() {
        return this.email.get();
    }
    
    public void setEmail(String value) {
        this.email.set(value);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getAddress() {
        return this.address.get();
    }
    
    public void setAddress(String value) {
        this.address.set(value);
    }

    public StringProperty addressProperty() {
        return address;
    }

    public String getPlatform() {
        return this.platform.get();
    }
    
    public void setPlatform(String value) {
        this.platform.set(value);
    }

    public StringProperty platformProperty() {
        return platform;
    }
}
