package com.daidaisuki.inventory.model;

import java.time.LocalDate;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Order {
    private final ObjectProperty<Customer> customer = new SimpleObjectProperty<Customer>(this, "customer");
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(this, "date");
    private final IntegerProperty id = new SimpleIntegerProperty(this, "id", -1);
    private final IntegerProperty totalItems = new SimpleIntegerProperty(this, "totalItems", 0);
    private final DoubleProperty totalAmount = new SimpleDoubleProperty(this, "totalAmount", 0);
    private final DoubleProperty discountAmount = new SimpleDoubleProperty(this, "discountAmount", 0);
    private final StringProperty paymentMethod = new SimpleStringProperty(this, "paymentMethod", "");
    private final ObservableList<OrderItem> items = FXCollections.observableArrayList();
    private final ChangeListener<Number> totalsUpdater = (obs, oldVal, newVal) -> updateTotals();
    private final WeakChangeListener<Number> weakTotalsUpdater = new WeakChangeListener<>(totalsUpdater);
    
    public Order() {
        items.addListener((ListChangeListener<OrderItem>) change -> {
            while(change.next()) {
                if(change.wasAdded()) {
                    for(OrderItem item : change.getAddedSubList()) {
                        item.quantityProperty().addListener(weakTotalsUpdater);
                        item.subtotalProperty().addListener(weakTotalsUpdater);
                    }
                }
                if(change.wasRemoved()) {
                    for(OrderItem item : change.getRemoved()) {
                        item.quantityProperty().removeListener(weakTotalsUpdater);
                        item.subtotalProperty().removeListener(weakTotalsUpdater);
                    }
                }
            }
            updateTotals();
        });
    }

    public void updateTotals() {
        totalItems.set(items.stream().mapToInt(OrderItem::getQuantity).sum());
        totalAmount.set(items.stream().mapToDouble(OrderItem::getSubtotal).sum());
    }

    public final Customer getCustomer() {
        return this.customer.get();
    }

    public final void setCustomer(Customer customer) {
        this.customer.set(customer);
    }

    public ObjectProperty<Customer> customerProperty() {
        return customer;
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

    public DoubleProperty discountAmountProperty() {
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

    public ObservableList<OrderItem> getItems() {
        return this.items;
    }
}
