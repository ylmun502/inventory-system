package com.daidaisuki.inventory.model;

import com.daidaisuki.inventory.model.base.BaseModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer extends BaseModel {
  private final StringProperty fullName = new SimpleStringProperty(this, "fullName", "");
  private final StringProperty phoneNumber = new SimpleStringProperty(this, "phoneNumber", "");
  private final StringProperty email = new SimpleStringProperty(this, "email", "");
  private final StringProperty address = new SimpleStringProperty(this, "address", "");
  private final StringProperty acquisitionSource =
      new SimpleStringProperty(this, "acquisitionSource", "");
  private final IntegerProperty totalOrders = new SimpleIntegerProperty(this, "totalOrders", 0);
  private final ObjectProperty<BigDecimal> totalSpent =
      new SimpleObjectProperty<>(this, "totalSpent", decimalZero());
  private final ObjectProperty<BigDecimal> totalDiscount =
      new SimpleObjectProperty<>(this, "totalDiscount", decimalZero());
  private final ObjectProperty<BigDecimal> averageOrderValue =
      new SimpleObjectProperty<>(this, "averageOrderValue", decimalZero());
  private final ReadOnlyObjectWrapper<OffsetDateTime> lastOrderDate =
      new ReadOnlyObjectWrapper<>(this, "lastOrderDate");

  public Customer() {
    super(-1, OffsetDateTime.now(), OffsetDateTime.now(), false);
  }

  public Customer(
      int id,
      String fullName,
      String phoneNumber,
      String email,
      String address,
      String acquisitionSource,
      int totalOrders,
      BigDecimal totalSpent,
      BigDecimal totalDiscount,
      BigDecimal averageOrderValue,
      OffsetDateTime lastOrderDate,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt,
      boolean deleted) {
    super(id, createdAt, updatedAt, deleted);
    this.fullName.set(fullName);
    this.phoneNumber.set(phoneNumber);
    this.email.set(email);
    this.address.set(address);
    this.acquisitionSource.set(acquisitionSource);
    this.totalOrders.set(totalOrders);
    this.totalSpent.set(sanitize(totalSpent));
    this.totalDiscount.set(sanitize(totalDiscount));
    this.averageOrderValue.set(sanitize(averageOrderValue));
    this.lastOrderDate.set(lastOrderDate);
  }

  public final String getFullName() {
    return this.fullName.get();
  }

  public final void setFullName(String fullName) {
    this.fullName.set(fullName);
  }

  public final StringProperty fullNameProperty() {
    return this.fullName;
  }

  public final String getPhoneNumber() {
    return this.phoneNumber.get();
  }

  public final void setPhoneNumber(String phoneNumber) {
    this.phoneNumber.set(phoneNumber);
  }

  public final StringProperty phoneNumberProperty() {
    return this.phoneNumber;
  }

  public final String getEmail() {
    return this.email.get();
  }

  public final void setEmail(String email) {
    this.email.set(email);
  }

  public final StringProperty emailProperty() {
    return this.email;
  }

  public final String getAddress() {
    return this.address.get();
  }

  public final void setAddress(String address) {
    this.address.set(address);
  }

  public final StringProperty addressProperty() {
    return this.address;
  }

  public final String getAcquisitionSource() {
    return this.acquisitionSource.get();
  }

  public final void setAcquisitionSource(String acquisitionSource) {
    this.acquisitionSource.set(acquisitionSource);
  }

  public final StringProperty acquisitionSourceProperty() {
    return this.acquisitionSource;
  }

  public final int getTotalOrders() {
    return this.totalOrders.get();
  }

  public final void setTotalOrders(int totalOrders) {
    this.totalOrders.set(totalOrders);
  }

  public final IntegerProperty totalOrdersProperty() {
    return this.totalOrders;
  }

  public final BigDecimal getTotalSpent() {
    return this.totalSpent.get();
  }

  public final ReadOnlyObjectProperty<BigDecimal> totalSpentProperty() {
    return this.totalSpent;
  }

  public final BigDecimal getTotalDiscount() {
    return this.totalDiscount.get();
  }

  public final ReadOnlyObjectProperty<BigDecimal> totalDiscountProperty() {
    return this.totalDiscount;
  }

  public final BigDecimal getAverageOrderValue() {
    return this.averageOrderValue.get();
  }

  public final ReadOnlyObjectProperty<BigDecimal> averageOrderValueProperty() {
    return this.averageOrderValue;
  }

  public final OffsetDateTime getLastOrderDate() {
    return this.lastOrderDate.get();
  }

  public final void setLastOrderDate(OffsetDateTime lastOrderDate) {
    this.lastOrderDate.set(lastOrderDate);
  }

  public final ReadOnlyObjectProperty<OffsetDateTime> lastOrderDateProperty() {
    return this.lastOrderDate.getReadOnlyProperty();
  }

  @Override
  public String toString() {
    if (this.getEmail() == null || this.getEmail().isEmpty()) return this.getFullName();
    return this.getFullName() + " (" + this.getEmail() + ")";
  }

  private BigDecimal decimalZero() {
    return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal sanitize(BigDecimal value) {
    return value == null ? decimalZero() : value.setScale(2, RoundingMode.HALF_UP);
  }
}
