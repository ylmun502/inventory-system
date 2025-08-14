package com.daidaisuki.inventory.model;

import com.daidaisuki.inventory.model.Product;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class OrderItem {
    private final ObjectProperty<Product> product = new SimpleObjectProperty<>();
    private final IntegerProperty id = new SimpleIntegerProperty(-1);
    private final IntegerProperty quantity = new SimpleIntegerProperty(0);
    private final DoubleProperty costAtSale = new SimpleDoubleProperty(0.0);
    private final BooleanProperty completed = new SimpleBooleanProperty(false);

    private final ReadOnlyDoubleWrapper subtotal = new ReadOnlyDoubleWrapper();
    private final ReadOnlyStringWrapper productName = new ReadOnlyStringWrapper("");

    public OrderItem() {
        initBindings();
    }

    public OrderItem(int id, Product product, int quantity, double costAtSale) {
        this.id.set(id);
        this.product.set(product);
        this.quantity.set(quantity);
        this.costAtSale.set(costAtSale);
        this.completed.set(costAtSale > 0);
        initBindings();
    }

    private void initBindings() {
        productName.bind(Bindings.createStringBinding(
            () -> product.get() != null ? product.get().getName() : "", product
        ));
        subtotal.bind(Bindings.createDoubleBinding( () -> {
            if(isOrderCompleted()) {
                return costAtSale.get() * quantity.get();
            }
            return (product.get() != null ? product.get().getPrice() : 0.0) * quantity.get();
        }, costAtSale, quantity, product));
    }

    private boolean isOrderCompleted() {
        return costAtSale.get() > 0;
    }

    public void finalizeSale() {
        if(product.get() != null) {
            costAtSale.set(product.get().getPrice());
            completed.set(true);
        }
    }

    public Product getProduct() {
        return product.get();
    }

    public void setProduct(Product product) {
        this.product.set(product);
    }

    public ObjectProperty<Product> productProperty() {
        return product;
    }

    public int getId() {
        return this.id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }
    
    public IntegerProperty idProperty() {
        return id;
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

    public double getCostAtSale() {
        return costAtSale.get();
    }

    public void setCostAtSale(double value) {
        this.costAtSale.set(value);
    }

    public DoubleProperty costAtSaleProperty() {
        return this.costAtSale;
    }

    public boolean getCompleted() {
        return completed.get();
    }

    public void setCompleted(boolean value) {
        completed.set(value);
    }

    public BooleanProperty completedProperty() {
        return completed;
    }

    public double getSubtotal() {
        return subtotal.get();
    }

    public ReadOnlyDoubleProperty subtotalProperty() {
        return subtotal.getReadOnlyProperty();
    }

    public String getProductName() {
        return productName.get();
    }

    public ReadOnlyStringProperty productNameProperty() {
        return productName.getReadOnlyProperty();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }
        OrderItem other = (OrderItem)obj;
        if(this.getId() == -1 || other.getId() == -1) {
            return this == other;
        }
        return id.get() == other.id.get();
    }

    @Override
    public int hashCode() {
        return (getId() == -1) ? System.identityHashCode(this) : Integer.hashCode(getId());
    }
}
