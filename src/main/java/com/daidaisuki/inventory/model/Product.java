package com.daidaisuki.inventory.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a product in the inventory system.
 * <p>
 * This class uses JavaFX {@code Property} types to support UI data binding,
 * such as displaying and editing product information in TableView columns or forms.
 * </p>
 */

public class Product {
    private int id;
    private final StringProperty name;
    private final StringProperty category;
    private final IntegerProperty stock;
    private final DoubleProperty price;
    private final DoubleProperty cost;
    private final DoubleProperty shipping;

    /**
     * Creates an empty product with default values.
     */
    public Product() {
        this.id = -1;
        this.name = new SimpleStringProperty("");
        this.category = new SimpleStringProperty("");
        this.stock = new SimpleIntegerProperty(0);
        this.price = new SimpleDoubleProperty(0.0);
        this.cost = new SimpleDoubleProperty(0.0);
        this.shipping = new SimpleDoubleProperty(0.0);
    }

    /**
     * Constructs a product with the given details.
     *
     * @param id       the product ID
     * @param name     the product name
     * @param category the product category
     * @param stock    the quantity in stock
     * @param price    the selling price
     * @param cost     the purchase price
     * @param shipping the shipping cost
     */
    public Product(int id, String name, String category, int stock, double price, double cost, double shipping) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.stock = new SimpleIntegerProperty(stock);
        this.price = new SimpleDoubleProperty(price);
        this.cost = new SimpleDoubleProperty(cost);
        this.shipping = new SimpleDoubleProperty(shipping);
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

    public String getCategory() { 
        return this.category.get();
    }

    public void setCategory(String value) {
        this.category.set(value);
    }

    public StringProperty categoryProperty() {
        return this.category;
    }

    public int getStock() { 
        return this.stock.get();
    }

    public void setStock(int value) {
        this.stock.set(value);
    }

    public IntegerProperty stockProperty() {
        return this.stock;
    }

    public double getPrice() { 
        return this.price.get();
    }

    public void setPrice(double value) {
        this.price.set(value);
    }

    public DoubleProperty priceProperty() {
        return this.price;
    }

    public double getCost() {
        return this.cost.get();
    }

    public void setCost(double value) {
        this.cost.set(value);
    }

    public DoubleProperty costProperty() {
        return this.cost;
    }

    public double getShipping() {
        return this.shipping.get();
    }

    public void setShipping(double value) {
        this.shipping.set(value);
    }

    public DoubleProperty shippingProperty() {
        return this.shipping;
    }

    /**
     * Returns {@code true} if the product has at least one item in stock.
     *
     * @return {@code true} if stock > 0; otherwise {@code false}
     */
    public boolean isInStock() {
        return getStock() > 0;
    }
}
