package com.daidaisuki.inventory.model.builder;

import com.daidaisuki.inventory.model.Supplier;
import java.time.OffsetDateTime;

public class SupplierBuilder {
  private int id = 1;
  private String name = "Test Supplier";
  private String shortCode = "SUP01";
  private String email = "supplier@example.com";
  private String phone = "1234567890";
  private String address = "123 Main St";
  private OffsetDateTime createdAt = OffsetDateTime.parse("2026-01-01T10:00:00Z");
  private OffsetDateTime updatedAt = OffsetDateTime.parse("2026-01-02T10:00:00Z");
  private boolean deleted = false;

  public SupplierBuilder withId(int id) {
    this.id = id;
    return this;
  }

  public SupplierBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public SupplierBuilder withShortCode(String shortCode) {
    this.shortCode = shortCode;
    return this;
  }

  public SupplierBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public SupplierBuilder withPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public SupplierBuilder withAddress(String address) {
    this.address = address;
    return this;
  }

  public SupplierBuilder withCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public SupplierBuilder withUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public SupplierBuilder deleted() {
    this.deleted = true;
    return this;
  }

  public Supplier build() {
    return new Supplier(
        this.id,
        this.name,
        this.shortCode,
        this.email,
        this.phone,
        this.address,
        this.createdAt,
        this.updatedAt,
        this.deleted);
  }
}
