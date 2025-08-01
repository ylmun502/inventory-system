package com.daidaisuki.inventory.model;

import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.model.OrderItem;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

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
    private final IntegerProperty id;
    private final IntegerProperty customerId;
    private final ObjectProperty<LocalDate> date;
    private final IntegerProperty totalItems;
    private final DoubleProperty totalAmount;
    private final DoubleProperty discountAmount;
    private final StringProperty paymentMethod;
    private List<OrderItem> items = new ArrayList<>();
    
    public Order() {
        this.customer = null;
        this.id = new SimpleIntegerProperty(-1);
        this.customerId = new SimpleIntegerProperty(-1);
        this.date = new SimpleObjectProperty<>();
        this.totalItems = new SimpleIntegerProperty(0);
        this.totalAmount = new SimpleDoubleProperty(0.0);
        this.discountAmount = new SimpleDoubleProperty(0.0);
        this.paymentMethod = new SimpleStringProperty("");
    }

    public Order(int id, int customerId, LocalDate date, int totalItems, double totalAmount, double discountAmount, String paymentMethod) {
        this.id = new SimpleIntegerProperty(id);
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
        if(customer != null) {
            this.customerId.set(customer.getId());
        }
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

    public void setTotalAmount(double value) {
        this.totalAmount.set(value);
    }

    public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    public double getDiscountAmount() {
        return this.discountAmount.get();
    }

    public void setDiscountAmount(double value) {
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

    public List<OrderItem> getItems() {
        return this.items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void recalculateTotals() {
        int totalItems = 0;
        double totalAmount = 0.0;
        
        for(OrderItem item : items) {
            totalItems += item.getQuantity();
            totalAmount += item.getSubtotal();
        }

        setTotalItems(totalItems);
        setTotalAmount(totalAmount);
    }
}
