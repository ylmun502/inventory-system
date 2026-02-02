package com.daidaisuki.inventory.model.builder;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ProductBuilder {
  // 1. Set "Sane Defaults" for all 14 fields
  private int id = 101;
  private String sku = "SKU-001";
  private String name = "Default Product";
  private long price = 1000L;
  private int stock = 10;
  private OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

  // 2. Add "With" methods only for fields you often need to change
  public ProductBuilder withId(int id) {
    this.id = id;
    return this;
  }

  public ProductBuilder withSku(String sku) {
    this.sku = sku;
    return this;
  }

  public ProductBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public ProductBuilder withStock(int stock) {
    this.stock = stock;
    return this;
  }

  public ProductBuilder withPrice(long price) {
    this.price = price;
    return this;
  }

  public ProductBuilder withCreatedAt(OffsetDateTime now) {
    this.now = now;
    return this;
  }

  // 3. The "Build" method calls the actual Product constructor
  /*public Product build() {
    return new Product(
        id, sku, "BAR-001", name, "Electronics", "Desc", 0, stock, price, 5, true, now, now, false);
  }*/
}
