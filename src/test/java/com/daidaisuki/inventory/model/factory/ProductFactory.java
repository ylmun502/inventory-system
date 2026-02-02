package com.daidaisuki.inventory.model.factory;

import com.daidaisuki.inventory.model.builder.ProductBuilder;

public class ProductFactory {
  // Returns the Builder so you can still customize it
  public static ProductBuilder aStandardProduct() {
    return new ProductBuilder();
  }

  /*
  // A specific preset for an out-of-stock product
  public static Product anEmptyProduct(int id) {
    return new ProductBuilder().withId(id).withStock(0).build();
  }
    */
}
