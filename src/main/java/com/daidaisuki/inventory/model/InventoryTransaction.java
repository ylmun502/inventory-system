package com.daidaisuki.inventory.model;

import com.daidaisuki.inventory.enums.TransactionType;
import com.daidaisuki.inventory.model.base.BaseModel;
import java.time.OffsetDateTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InventoryTransaction extends BaseModel {
  private final ReadOnlyIntegerWrapper productId =
      new ReadOnlyIntegerWrapper(this, "productId", -1);
  private final ReadOnlyIntegerWrapper batchId = new ReadOnlyIntegerWrapper(this, "batchId", -1);
  private final ReadOnlyIntegerWrapper userId = new ReadOnlyIntegerWrapper(this, "userId", -1);
  private final ReadOnlyIntegerWrapper referenceId =
      new ReadOnlyIntegerWrapper(this, "referenceId", -1);
  private final IntegerProperty changeAmount = new SimpleIntegerProperty(this, "changeAmount", 0);
  private final ObjectProperty<TransactionType> transactionType =
      new SimpleObjectProperty<>(this, "transactionType", TransactionType.STOCK_IN);
  private final StringProperty reasonCode = new SimpleStringProperty(this, "reasonCode", "");
  private final ObjectProperty<Product> product = new SimpleObjectProperty<>(this, "product");
  private final ObjectProperty<StockBatch> batch = new SimpleObjectProperty<>(this, "batch");
  private final ReadOnlyStringWrapper productName = new ReadOnlyStringWrapper();

  public InventoryTransaction() {
    super(-1, OffsetDateTime.now(), OffsetDateTime.now(), false);
    initBindings();
  }

  public InventoryTransaction(
      int id,
      int productId,
      int batchId,
      int userId,
      int referenceId,
      int changeAmount,
      TransactionType transactionType,
      String reasonCode,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt,
      boolean deleted) {
    super(id, createdAt, updatedAt, deleted);
    this.productId.set(productId);
    this.batchId.set(batchId);
    this.userId.set(userId);
    this.referenceId.set(referenceId);
    this.changeAmount.set(changeAmount);
    this.transactionType.set(transactionType);
    this.reasonCode.set(reasonCode);
    initBindings();
  }

  public void initBindings() {
    productName.bind(product.flatMap(Product::nameProperty).orElse("No Product Assigned"));
  }

  public final int getProductId() {
    return this.productId.get();
  }

  public final ReadOnlyIntegerProperty productIdProperty() {
    return this.productId.getReadOnlyProperty();
  }

  public final int getBatchId() {
    return this.batchId.get();
  }

  public final ReadOnlyIntegerProperty batchIdProperty() {
    return this.batchId.getReadOnlyProperty();
  }

  public final int getUserId() {
    return this.userId.get();
  }

  public final ReadOnlyIntegerProperty userIdProperty() {
    return this.userId.getReadOnlyProperty();
  }

  public final int getReferenceId() {
    return this.referenceId.get();
  }

  public final ReadOnlyIntegerProperty referenceIdProperty() {
    return this.referenceId.getReadOnlyProperty();
  }

  public final int getChangeAmount() {
    return this.changeAmount.get();
  }

  public final void setChangeAmount(int changeAmount) {
    this.changeAmount.set(changeAmount);
  }

  public final IntegerProperty changeAmountProperty() {
    return this.changeAmount;
  }

  public final TransactionType getTransactionType() {
    return this.transactionType.get();
  }

  public final void setTransactionType(TransactionType transactionType) {
    this.transactionType.set(transactionType);
  }

  public final ObjectProperty<TransactionType> transactionTypeProperty() {
    return this.transactionType;
  }

  public final String getReasonCode() {
    return this.reasonCode.get();
  }

  public final void setReasonCode(String reasonCode) {
    this.reasonCode.set(reasonCode);
  }

  public final StringProperty reasonCodeProperty() {
    return this.reasonCode;
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

  public final StockBatch getBatch() {
    return this.batch.get();
  }

  public final void setBatch(StockBatch batch) {
    this.batch.set(batch);
    this.batchId.set(batch != null ? batch.getId() : -1);
  }

  public final ObjectProperty<StockBatch> batchProperty() {
    return this.batch;
  }

  public boolean isReduction() {
    return getChangeAmount() < 0;
  }
}
