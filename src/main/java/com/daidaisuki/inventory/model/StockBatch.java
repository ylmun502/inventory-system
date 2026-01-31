package com.daidaisuki.inventory.model;

import com.daidaisuki.inventory.model.base.BaseModel;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StockBatch extends BaseModel {
  private final ReadOnlyIntegerWrapper productId =
      new ReadOnlyIntegerWrapper(this, "productId", -1);
  private final ReadOnlyIntegerWrapper supplierId =
      new ReadOnlyIntegerWrapper(this, "supplierId", -1);
  private final StringProperty batchCode = new SimpleStringProperty(this, "batchCode", "");
  private final ObjectProperty<OffsetDateTime> expiryDate =
      new SimpleObjectProperty<>(this, "expiryDate");
  private final IntegerProperty quantityReceived =
      new SimpleIntegerProperty(this, "quantityReceived", 0);
  private final IntegerProperty quantityRemaining =
      new SimpleIntegerProperty(this, "quantityRemaining", 0);
  private final ObjectProperty<BigDecimal> unitCost =
      new SimpleObjectProperty<>(this, "unitCost", BigDecimal.ZERO);
  private final ObjectProperty<BigDecimal> landedCost =
      new SimpleObjectProperty<>(this, "landedCost", BigDecimal.ZERO);
  private final ObjectProperty<Product> product = new SimpleObjectProperty<>(this, "product");
  // Transient/Calculated properties for UI Bindings
  private final ReadOnlyStringWrapper productName = new ReadOnlyStringWrapper(this, "productName");
  private final ReadOnlyObjectWrapper<BigDecimal> totalValue =
      new ReadOnlyObjectWrapper<>(this, "totalValue", BigDecimal.ZERO);

  public StockBatch(int productId, int supplierId, int quantity, BigDecimal unitCost) {
    super(-1, OffsetDateTime.now(), OffsetDateTime.now(), false);
    this.productId.set(productId);
    this.supplierId.set(supplierId);
    this.quantityReceived.set(quantity);
    this.quantityRemaining.set(quantity);
    this.unitCost.set(unitCost);
    this.landedCost.set(unitCost);
    initBindings();
  }

  public StockBatch(
      int id,
      int productId,
      int supplierId,
      String batchCode,
      OffsetDateTime expiryDate,
      int quantityReceived,
      int quantityRemaining,
      BigDecimal unitCost,
      BigDecimal landedCost,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt,
      boolean deleted) {
    super(id, createdAt, updatedAt, deleted);
    this.productId.set(productId);
    this.supplierId.set(supplierId);
    this.batchCode.set(batchCode);
    this.expiryDate.set(expiryDate);
    this.quantityReceived.set(quantityReceived);
    this.quantityRemaining.set(quantityRemaining);
    this.unitCost.set(unitCost);
    this.landedCost.set(landedCost);
    initBindings();
  }

  private void initBindings() {
    this.productName.bind(
        this.product.flatMap(Product::nameProperty).orElse("No Product Assigned"));
    // Example of the JavaFX binding pattern seen in your OrderItem
    this.totalValue.bind(
        Bindings.createObjectBinding(
            () -> {
              return BigDecimal.valueOf(this.quantityRemaining.get())
                  .multiply(this.landedCost.get());
            },
            this.quantityRemaining,
            this.landedCost));
    /* Low-level functional binding for custom logic and optimization
    totalValue.bind(Bindings.createDoubleBinding(
        () -> (double) (getQuantityRemaining() * getLandedCostCents()) / 100.0,
        quantityRemaining, landedCostCents
    ));
    */
  }

  public final int getProductId() {
    return this.productId.get();
  }

  public final ReadOnlyIntegerProperty productIdProperty() {
    return this.productId.getReadOnlyProperty();
  }

  public final int getSupplierId() {
    return this.supplierId.get();
  }

  public final ReadOnlyIntegerProperty supplierIdProperty() {
    return this.supplierId.getReadOnlyProperty();
  }

  public final String getBatchCode() {
    return this.batchCode.get();
  }

  public final void setBatchCode(String batchCode) {
    this.batchCode.set(batchCode);
  }

  public final StringProperty batchCodeProperty() {
    return this.batchCode;
  }

  public final OffsetDateTime getExpiryDate() {
    return this.expiryDate.get();
  }

  public final void setExpiryDate(OffsetDateTime expiryDate) {
    this.expiryDate.set(expiryDate);
  }

  public final ObjectProperty<OffsetDateTime> expiryDateProperty() {
    return this.expiryDate;
  }

  public final int getQuantityReceived() {
    return this.quantityReceived.get();
  }

  public final void setQuantityReceived(int quantityReceived) {
    this.quantityReceived.set(quantityReceived);
  }

  public final IntegerProperty quantityReceivedProperty() {
    return this.quantityReceived;
  }

  public final int getQuantityRemaining() {
    return this.quantityRemaining.get();
  }

  public final void setQuantityRemaining(int quantityRemaining) {
    this.quantityRemaining.set(quantityRemaining);
  }

  public final IntegerProperty quantityRemainingProperty() {
    return this.quantityRemaining;
  }

  public final BigDecimal getUnitCost() {
    return this.unitCost.get();
  }

  public final void setUnitCost(BigDecimal unitCost) {
    this.unitCost.set(unitCost);
  }

  public final ObjectProperty<BigDecimal> unitCostProperty() {
    return this.unitCost;
  }

  public final BigDecimal getLandedCost() {
    return this.landedCost.get();
  }

  public final void setLandedCost(BigDecimal landedCost) {
    this.landedCost.set(landedCost);
  }

  public final ObjectProperty<BigDecimal> landedCostProperty() {
    return this.landedCost;
  }

  public final Product getProduct() {
    return this.product.get();
  }

  public final void setProduct(Product product) {
    this.product.set(product);
    this.productId.set(product != null ? product.getId() : -1);
  }

  public final ObjectProperty<Product> productProperty() {
    return this.product;
  }

  public final String getProductName() {
    return this.productName.get();
  }

  public final ReadOnlyStringProperty productNameProperty() {
    return this.productName.getReadOnlyProperty();
  }

  public final ReadOnlyObjectProperty<BigDecimal> totalValueProperty() {
    return this.totalValue.getReadOnlyProperty();
  }

  public final BigDecimal getTotalValue() {
    return this.totalValue.get();
  }

  public final boolean isAvailable() {
    return this.getQuantityRemaining() > 0;
  }

  public final BigDecimal getPotentialProfit() {
    Product currentProduct = this.getProduct();
    if (currentProduct == null) {
      return BigDecimal.ZERO;
    }
    return currentProduct.getSellingPrice().subtract(this.getUnitCost());
  }
}
