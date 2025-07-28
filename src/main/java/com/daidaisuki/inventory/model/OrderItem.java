package com.daidaisuki.inventory.model;

import com.daidaisuki.inventory.model.Product;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class OrderItem {
    private Product product;
    private int id;
    private final IntegerProperty orderId;
    private final IntegerProperty productId;
    private final IntegerProperty quantity;
    private final DoubleProperty unitPrice;
    private final DoubleProperty costAtSale;

    private final ReadOnlyDoubleWrapper subtotal = new ReadOnlyDoubleWrapper();

    public OrderItem(Product product, int quantity) {
        this.id = -1;
        this.product = product;
        this.orderId = new SimpleIntegerProperty(-1);
        this.productId = new SimpleIntegerProperty(product.getId());
        this.quantity = new SimpleIntegerProperty(quantity);
        this.unitPrice = new SimpleDoubleProperty(product.getPrice());
        this.costAtSale = new SimpleDoubleProperty(product.getCost());
        bindSubtotal();
    }

    public OrderItem(int id, int orderId, int productId, int quantity, double unitPrice, double costAtSale) {
        this.id = id;
        this.orderId = new SimpleIntegerProperty(orderId);
        this.productId = new SimpleIntegerProperty(productId);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.unitPrice = new SimpleDoubleProperty(unitPrice);
        this.costAtSale = new SimpleDoubleProperty(costAtSale);
        bindSubtotal();
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return this.orderId.get();
    }

    public void setOrderId(int value) {
        this.orderId.set(value);
    }

    public IntegerProperty orderIdProperty() {
        return this.orderId;
    }

    public int getProductId() {
        return this.productId.get();
    }

    public void setProductId(int value) {
        this.productId.set(value);
    }

    public IntegerProperty productIdProperty() {
        return this.productId;
    }

    public int getQuantity() {
        return this.quantity.get();
    }

    public void setQuantity(int value) {
        this.quantity.set(value);
    }

    public IntegerProperty quantityProperty() {
        return this.quantity;
    }

    public double getUnitPrice() {
        return this.unitPrice.get();
    }

    public void setUnitPrice(double value) {
        this.unitPrice.set(value);
    }

    public DoubleProperty unitPriceProperty() {
        return this.unitPrice;
    }

    public double getCostAtSale() {
        return costAtSale.get();
    }

    public void setCostAtSale(double value) {
        this.costAtSale.set(value);
    }

    public DoubleProperty costAtSaleProperty() {
        return this.costAtSale;
    }

    private void bindSubtotal() {
        subtotal.bind(this.quantity.multiply(this.unitPrice));
    }

    public ReadOnlyDoubleProperty subtotalProperty() {
        return subtotal.getReadOnlyProperty();
    }

    public double getSubtotal() {
        return subtotal.get();
    }
}
