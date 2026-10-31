package com.daidaisuki.inventory.model.builder;

import com.daidaisuki.inventory.model.Customer;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class CustomerBuilder {
  private int id = -1;
  private String fullName = "Test Customer";
  private String phoneNumber = "1234567890";
  private String email = "supplier@example.com";
  private String address = "123 Main St";
  private String acquisitionSource = "Instagram";
  private int totalOrders = 8;
  private BigDecimal totalSpent = new BigDecimal("180.00");
  private BigDecimal totalDiscount = new BigDecimal("18.00");
  private BigDecimal averageOrderValue = new BigDecimal("22.50");
  private OffsetDateTime lastOrderDate = OffsetDateTime.parse("2026-01-10T10:00:00Z");
  private OffsetDateTime createdAt = OffsetDateTime.parse("2026-01-01T10:00:00Z");
  private OffsetDateTime updatedAt = OffsetDateTime.parse("2026-01-02T10:00:00Z");
  private boolean deleted = false;

  public CustomerBuilder withId(int id) {
    this.id = id;
    return this;
  }

  public CustomerBuilder withFullName(String fullName) {
    this.fullName = fullName;
    return this;
  }

  public CustomerBuilder withPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public CustomerBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public CustomerBuilder withAddress(String address) {
    this.address = address;
    return this;
  }

  public CustomerBuilder withAcquisitionSource(String acquisitionSource) {
    this.acquisitionSource = acquisitionSource;
    return this;
  }

  public CustomerBuilder withTotalOrders(int totalOrders) {
    this.totalOrders = totalOrders;
    return this;
  }

  public CustomerBuilder withTotalSpent(BigDecimal totalSpent) {
    this.totalSpent = totalSpent;
    return this;
  }

  public CustomerBuilder withTotalDiscount(BigDecimal totalDiscount) {
    this.totalDiscount = totalDiscount;
    return this;
  }

  public CustomerBuilder withAverageOrderValue(BigDecimal averageOrderValue) {
    this.averageOrderValue = averageOrderValue;
    return this;
  }

  public CustomerBuilder withLastOrderDate(OffsetDateTime lastOrderDate) {
    this.lastOrderDate = lastOrderDate;
    return this;
  }

  public CustomerBuilder withCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public CustomerBuilder withUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public CustomerBuilder deleted() {
    this.deleted = true;
    return this;
  }

  public Customer build() {
    return new Customer(
        this.id,
        this.fullName,
        this.phoneNumber,
        this.email,
        this.address,
        this.acquisitionSource,
        this.totalOrders,
        this.totalSpent,
        this.totalDiscount,
        this.averageOrderValue,
        this.lastOrderDate,
        this.createdAt,
        this.updatedAt,
        this.deleted);
  }
}
