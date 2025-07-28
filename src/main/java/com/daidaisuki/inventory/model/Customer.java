package com.daidaisuki.inventory.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty phoneNumber;
    private final StringProperty email;
    private final StringProperty address;
    private final StringProperty platform;

    private transient int totalOrders;
    private transient double totalSpent;
    private transient double totalDiscount;

    public Customer() {
        this.id = new SimpleIntegerProperty(-1);
        this.name = new SimpleStringProperty("");
        this.phoneNumber = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.address = new SimpleStringProperty("");
        this.platform = new SimpleStringProperty("");
    }

    public Customer(int id, String name, String phoneNumber, String email, String address, String platform) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.email = new SimpleStringProperty(email);
        this.address = new SimpleStringProperty(address);
        this.platform = new SimpleStringProperty(platform);
    }

    public int getId() {
        return this.id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return this.id;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Customer other = (Customer) obj;
        return this.getId() == other.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }

    @Override
    public String toString() {
        return getName();
    }

    public int getTotalOrders() {
        return this.totalOrders;
    }
    
    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public double getTotalSpent() {
        return this.totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public double getTotalDiscount() {
        return this.totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }
}
