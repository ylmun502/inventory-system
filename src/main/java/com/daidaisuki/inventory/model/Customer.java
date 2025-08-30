package com.daidaisuki.inventory.model;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {
    private final ReadOnlyIntegerWrapper id = new ReadOnlyIntegerWrapper(this, "id", -1);
    private final StringProperty name = new SimpleStringProperty(this, "name", "");
    private final StringProperty phoneNumber = new SimpleStringProperty(this, "phoneNumber", "");
    private final StringProperty email = new SimpleStringProperty(this, "email", "");
    private final StringProperty address = new SimpleStringProperty(this, "address", "");
    private final StringProperty platform = new SimpleStringProperty(this, "platform", "");

    private transient int totalOrders;
    private transient double totalSpent;
    private transient double totalDiscount;

    public Customer() {
    }

    public Customer(int id, String name, String phoneNumber, String email, String address, String platform) {
        this.id.set(id);
        this.name.set(name);
        this.phoneNumber.set(phoneNumber);
        this.email.set(email);
        this.address.set(address);
        this.platform.set(platform);
    }

    public int getId() {
        return this.id.get();
    }

    void setId(int id) {
        this.id.set(id);
    }

    public ReadOnlyIntegerProperty idProperty() {
        return id.getReadOnlyProperty();
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
