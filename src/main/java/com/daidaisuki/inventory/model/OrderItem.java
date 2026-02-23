package com.daidaisuki.inventory.model;

import com.daidaisuki.inventory.model.base.BaseModel;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

public class OrderItem extends BaseModel {
  private final ReadOnlyIntegerWrapper orderId = new ReadOnlyIntegerWrapper(this, "orderId", -1);
  private final ReadOnlyIntegerWrapper productId =
      new ReadOnlyIntegerWrapper(this, "productId", -1);
  private final ReadOnlyIntegerWrapper batchId = new ReadOnlyIntegerWrapper(this, "batchId", -1);

  private final IntegerProperty quantity = new SimpleIntegerProperty(0);
  private final LongProperty unitPriceAtSaleCents = new SimpleLongProperty(0L);
  private final LongProperty unitCostAtSaleCents = new SimpleLongProperty(0L);

  private final ObjectProperty<Product> product = new SimpleObjectProperty<>();

  private final ReadOnlyLongWrapper subtotal = new ReadOnlyLongWrapper();
  private final ReadOnlyStringWrapper productName = new ReadOnlyStringWrapper("");

  public OrderItem() {
    super(-1, OffsetDateTime.now(), OffsetDateTime.now(), false);
    initBindings();
  }

  public OrderItem(
      int id,
      int orderId,
      int productId,
      int batchId,
      int quantity,
      long unitPriceAtSaleCents,
      long unitCostAtSaleCents,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt,
      boolean deleted) {
    super(id, createdAt, updatedAt, deleted);
    initBindings();
    this.orderId.set(orderId);
    this.productId.set(productId);
    this.batchId.set(batchId);
    this.quantity.set(quantity);
    this.unitPriceAtSaleCents.set(unitPriceAtSaleCents);
    this.unitCostAtSaleCents.set(unitCostAtSaleCents);
  }

  public OrderItem(
      int orderId,
      int productId,
      int batchId,
      int quantity,
      long unitPriceAtSaleCents,
      long unitCostAtSaleCents) {
    super(-1, OffsetDateTime.now(ZoneOffset.UTC), OffsetDateTime.now(ZoneOffset.UTC), false);
    initBindings();
    this.orderId.set(orderId);
    this.productId.set(productId);
    this.batchId.set(batchId);
    this.quantity.set(quantity);
    this.unitPriceAtSaleCents.set(unitPriceAtSaleCents);
    this.unitCostAtSaleCents.set(unitCostAtSaleCents);
  }

  public OrderItem(Product product, int quantity) {
    this();
    this.product.set(product);
    this.productId.set(product != null ? product.getId() : -1);
    /* change this after refactoring the unitPriceAtSale to BigDecimal in OrderItem
    this.unitPriceAtSaleCents.set(product != null ? product.getSellingPriceCents() : 0);
    */
    this.quantity.set(quantity);
  }

  private void initBindings() {
    this.productName.bind(
        this.product.flatMap(Product::nameProperty).orElse("No Product Assigned"));
    this.subtotal.bind(
        Bindings.createLongBinding(
            () -> getUnitPriceAtSaleCents() * getQuantity(),
            this.unitPriceAtSaleCents,
            this.quantity));
    /* Use this logic for Prototyping, Temporary Data Entry, or Live Estimation Tools
    subtotal.bind(
        Bindings.createLongBinding(
            () -> {
              if (unitCostAtSaleCents.get() > 0) {
                return unitCostAtSaleCents.get() * quantity.get();
              }
              long priceToUse =
                  unitPriceCents.get() > 0
                      ? unitPriceCents.get()
                      : (product.get() != null ? product.get().getSellingPriceCents() : 0);
              return priceToUse * quantity.get();
            },
            unitCostAtSaleCents,
            unitPriceCents,
            quantity,
            product));
    */
  }

  public final int getOrderId() {
    return orderId.get();
  }

  public final ReadOnlyIntegerProperty orderIdProperty() {
    return orderId.getReadOnlyProperty();
  }

  public final int getProductId() {
    return productId.get();
  }

  public final ReadOnlyIntegerProperty productIdProperty() {
    return productId.getReadOnlyProperty();
  }

  public final int getBatchId() {
    return batchId.get();
  }

  public final ReadOnlyIntegerProperty batchIdProperty() {
    return batchId.getReadOnlyProperty();
  }

  public final int getQuantity() {
    return quantity.get();
  }

  public final void setQuantity(int quantity) {
    this.quantity.set(quantity);
  }

  public final IntegerProperty quantityProperty() {
    return quantity;
  }

  public final long getUnitPriceAtSaleCents() {
    return unitPriceAtSaleCents.get();
  }

  public final void setUnitPriceAtSaleCents(long unitPriceCents) {
    this.unitPriceAtSaleCents.set(unitPriceCents);
  }

  public final LongProperty unitPriceAtSaleCentsProperty() {
    return unitPriceAtSaleCents;
  }

  public final long getUnitCostAtSaleCents() {
    return unitCostAtSaleCents.get();
  }

  public final void setUnitCostAtSaleCents(long costAtSaleCents) {
    this.unitCostAtSaleCents.set(costAtSaleCents);
  }

  public final LongProperty unitCostAtSaleCentsProperty() {
    return unitCostAtSaleCents;
  }

  public final Product getProduct() {
    return product.get();
  }

  public final void setProduct(Product product) {
    this.product.set(product);
    this.productId.set(product != null ? product.getId() : -1);
    if (product != null && unitPriceAtSaleCents.get() <= 0) {
      /* change this after refactoring the unitPriceAtSale to BigDecimal in OrderItem
      unitPriceAtSaleCents.set(product.getSellingPriceCents());
      */
    }
  }

  public final ObjectProperty<Product> productProperty() {
    return product;
  }

  public final long getSubtotal() {
    return subtotal.get();
  }

  public final ReadOnlyLongProperty subtotalProperty() {
    return subtotal.getReadOnlyProperty();
  }

  public final String getProductName() {
    return productName.get();
  }

  public final ReadOnlyStringProperty productNameProperty() {
    return productName.getReadOnlyProperty();
  }
}
