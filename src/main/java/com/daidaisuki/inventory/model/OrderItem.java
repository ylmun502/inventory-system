package com.daidaisuki.inventory.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class OrderItem {
  private final ObjectProperty<Product> product = new SimpleObjectProperty<>();
  private final IntegerProperty id = new SimpleIntegerProperty(-1);
  private final IntegerProperty orderId = new SimpleIntegerProperty(-1);
  private final IntegerProperty productId = new SimpleIntegerProperty(-1);
  private final IntegerProperty quantity = new SimpleIntegerProperty(0);
  private final DoubleProperty unitPrice = new SimpleDoubleProperty(0.0);
  private final DoubleProperty costAtSale = new SimpleDoubleProperty(0.0);

  private final ReadOnlyDoubleWrapper subtotal = new ReadOnlyDoubleWrapper();
  private final ReadOnlyStringWrapper productName = new ReadOnlyStringWrapper("");

  public OrderItem() {
    initBindings();
  }

  public OrderItem(
      int id, int orderId, int productId, int quantity, double unitPrice, double costAtSale) {
    this.id.set(id);
    this.orderId.set(orderId);
    this.productId.set(productId);
    this.quantity.set(quantity);
    this.unitPrice.set(unitPrice);
    this.costAtSale.set(costAtSale);
    initBindings();
  }

  public OrderItem(Product product, int quantity) {
    this.product.set(product);
    this.productId.set(product != null ? product.getId() : -1);
    this.unitPrice.set(product != null ? product.getPrice() : 0.0);
    this.quantity.set(quantity);
    initBindings();
  }

  private void initBindings() {
    productName.bind(
        Bindings.createStringBinding(
            () -> product.get() != null ? product.get().getName() : "", product));
    subtotal.bind(
        Bindings.createDoubleBinding(
            () -> {
              if (costAtSale.get() > 0) {
                return costAtSale.get() * quantity.get();
              }
              double priceToUse =
                  unitPrice.get() > 0
                      ? unitPrice.get()
                      : (product.get() != null ? product.get().getPrice() : 0.0);
              return priceToUse * quantity.get();
            },
            costAtSale,
            unitPrice,
            quantity,
            product));
  }

  public Product getProduct() {
    return product.get();
  }

  public void setProduct(Product product) {
    this.product.set(product);
    this.productId.set(product != null ? product.getId() : -1);
    if (product != null && unitPrice.get() <= 0) {
      unitPrice.set(product.getPrice());
    }
  }

  public ObjectProperty<Product> productProperty() {
    return product;
  }

  public int getId() {
    return id.get();
  }

  public void setId(int id) {
    this.id.set(id);
  }

  public IntegerProperty idProperty() {
    return id;
  }

  public int getOrderId() {
    return orderId.get();
  }

  public void setOrderId(int orderId) {
    this.orderId.set(orderId);
  }

  public IntegerProperty orderIdProperty() {
    return orderId;
  }

  public int getProductId() {
    return productId.get();
  }

  public void setProductId(int productId) {
    this.productId.set(productId);
  }

  public IntegerProperty productIdProperty() {
    return productId;
  }

  public int getQuantity() {
    return quantity.get();
  }

  public void setQuantity(int quantity) {
    this.quantity.set(quantity);
  }

  public IntegerProperty quantityProperty() {
    return quantity;
  }

  public double getUnitPrice() {
    return unitPrice.get();
  }

  public void setUnitPrice(double price) {
    this.unitPrice.set(price);
  }

  public DoubleProperty unitPriceProperty() {
    return unitPrice;
  }

  public double getCostAtSale() {
    return costAtSale.get();
  }

  public void setCostAtSale(double cost) {
    this.costAtSale.set(cost);
  }

  public DoubleProperty costAtSaleProperty() {
    return costAtSale;
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
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    OrderItem other = (OrderItem) obj;
    if (this.getId() == -1 || other.getId() == -1) {
      return this == other;
    }
    return id.get() == other.getId();
  }

  @Override
  public int hashCode() {
    return (getId() == -1) ? System.identityHashCode(this) : Integer.hashCode(getId());
  }
}
