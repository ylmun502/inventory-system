package com.daidaisuki.inventory.model.builder;

import com.daidaisuki.inventory.model.Product;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class ProductBuilder {
  private int id = -1;
  private String sku = "PRD-001";
  private String barcode = "BAr-001";
  private String name = "Test Product";
  private String category = "Test Category";
  private String unitType = "each";
  private String taxCategory = "Standard";
  private String description = "Standard test product";
  private int weight = 100;
  private int currentStock = 50;
  private int minStockLevel = 5;
  private int maxStockLevel = 100;
  private int reorderingLevel = 10;
  private BigDecimal sellingPrice = new BigDecimal("39.99");
  private BigDecimal averageUnitCost = new BigDecimal("8.99");
  private boolean active = true;
  private OffsetDateTime createdAt = OffsetDateTime.parse("2026-01-01T10:00:00Z");
  private OffsetDateTime updatedAt = OffsetDateTime.parse("2026-01-02T10:00:00Z");
  private boolean deleted = false;

  public ProductBuilder withId(int id) {
    this.id = id;
    return this;
  }

  public ProductBuilder withSku(String sku) {
    this.sku = sku;
    return this;
  }

  public ProductBuilder withBarcode(String barcode) {
    this.barcode = barcode;
    return this;
  }

  public ProductBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public ProductBuilder withCategory(String category) {
    this.category = category;
    return this;
  }

  public ProductBuilder withUnitType(String unitType) {
    this.unitType = unitType;
    return this;
  }

  public ProductBuilder withTaxCategory(String taxCategory) {
    this.taxCategory = taxCategory;
    return this;
  }

  public ProductBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public ProductBuilder withWeight(int weight) {
    this.weight = weight;
    return this;
  }

  public ProductBuilder withCurrentStock(int currentStock) {
    this.currentStock = currentStock;
    return this;
  }

  public ProductBuilder withMinStockLevel(int minStockLevel) {
    this.minStockLevel = minStockLevel;
    return this;
  }

  public ProductBuilder withMaxStockLevel(int maxStockLevel) {
    this.maxStockLevel = maxStockLevel;
    return this;
  }

  public ProductBuilder withReorderingLevel(int reorderingLevel) {
    this.reorderingLevel = reorderingLevel;
    return this;
  }

  public ProductBuilder withSellingPrice(BigDecimal sellingPrice) {
    this.sellingPrice = sellingPrice;
    return this;
  }

  public ProductBuilder withAverageUnitCost(BigDecimal averageUnitCost) {
    this.averageUnitCost = averageUnitCost;
    return this;
  }

  public ProductBuilder inactive() {
    this.active = false;
    return this;
  }

  public ProductBuilder withCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public ProductBuilder withUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public ProductBuilder deleted() {
    this.deleted = true;
    return this;
  }

  public Product build() {
    return new Product(
        this.id,
        this.sku,
        this.barcode,
        this.name,
        this.category,
        this.unitType,
        this.taxCategory,
        this.description,
        this.weight,
        this.currentStock,
        this.minStockLevel,
        this.maxStockLevel,
        this.reorderingLevel,
        this.sellingPrice,
        this.averageUnitCost,
        this.active,
        this.createdAt,
        this.updatedAt,
        this.deleted);
  }
}
