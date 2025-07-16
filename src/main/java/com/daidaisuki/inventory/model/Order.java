package com.daidaisuki.inventory.model;

import java.time.LocalDate;

import com.daidaisuki.inventory.model.Customer;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public class Order {
    private Customer customer;
    private int id;
    private final IntegerProperty customerId;
    private final ObjectProperty<LocalDate> date;
    private final IntegerProperty totalItems;
    private final DoubleProperty totalAmount;
    private final DoubleProperty discountAmount;
    private final StringProperty paymentMethod;
    
    public Order(int id, int customerId, LocalDate date, int totalItems, double totalAmount, double discountAmount, String paymentMethod) {
        this.id = id;
        this.customerId = new SimpleIntegerProperty(customerId);
        this.date = new SimpleObjectProperty<>(date);
        this.totalItems = new SimpleIntegerProperty(totalItems);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.discountAmount = new SimpleDoubleProperty(discountAmount);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date.get();
    }

    public void setDate(LocalDate value) {
        this.date.set(value);
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public int getCustomerId() {
        return this.customerId.get();
    }

    public void setCustomerId(int value) {
        this.customerId.set(value);
    }

    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    public int getTotalItems() {
        return this.totalItems.get();
    }

    public void setTotalItems(int value) {
        this.totalItems.set(value);
    }

    public IntegerProperty totalItemsProperty() {
        return totalItems;
    }

    public double getTotalAmount() {
        return this.totalAmount.get();
    }

    public void setTotalAmount(int value) {
        this.totalAmount.set(value);
    }

    public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    public double getDiscountAmount() {
        return this.discountAmount.get();
    }

    public void setDiscountAmount(int value) {
        this.discountAmount.set(value);
    }

    public DoubleProperty totalDiscountProperty() {
        return discountAmount;
    }

    public String getPaymentMethod() {
        return this.paymentMethod.get();
    }

    public void setPaymentMethod(String value) {
        this.paymentMethod.set(value);
    }

    public StringProperty paymentMethodProperty() {
        return paymentMethod;
    }
}
