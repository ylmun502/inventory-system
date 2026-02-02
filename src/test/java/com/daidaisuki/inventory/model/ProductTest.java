package com.daidaisuki.inventory.model;

import static org.junit.jupiter.api.Assertions.*;

/*
public class ProductTest {
  @Test
  @DisplayName("Constructor should correctly initialize all properties")
  void testFullConstructor() {
    OffsetDateTime now = OffsetDateTime.now();

    // Use the builder to set specific values
    Product product =
        new ProductBuilder()
            .withId(1)
            .withSku("SKU123")
            .withName("Laptop")
            .withStock(10)
            .withPrice(150000L)
            .withCreatedAt(now)
            .build();

    assertEquals(1, product.getId());
    assertEquals("SKU123", product.getSku());
    assertEquals("Laptop", product.getName());
    assertEquals(10, product.getCurrentStock());
    assertEquals(150000L, product.getSellingPriceCents());
    assertTrue(product.isActive());
  }

  @Test
  @DisplayName("isInStock should return true only when currentStock > 0")
  void testIsInStock() {
    // Use factory for a clean default object
    Product product = ProductFactory.aStandardProduct().build();

    product.setCurrentStock(0);
    assertFalse(product.isInStock(), "Product should be out of stock when count is 0");

    product.setCurrentStock(5);
    assertTrue(product.isInStock(), "Product should be in stock when count is 5");
  }

  @Test
  @DisplayName("JavaFX Properties should allow data binding")
  void testPropertyBinding() {
    Product product = new Product();
    product.setName("Old Name");

    // This tests if the property system is actually working
    assertEquals("Old Name", product.nameProperty().get());

    product.nameProperty().set("New Name");
    assertEquals("New Name", product.getName());
  }

  @Test
  @DisplayName("equals should compare products based on ID")
  void testEquals() {
    Product p1 = ProductFactory.aStandardProduct().withId(500).build();
    Product p2 = ProductFactory.aStandardProduct().withId(500).build();
    Product p3 = ProductFactory.aStandardProduct().withId(999).build();

    assertEquals(p1, p2, "Products with same ID should be equal");
    assertNotEquals(p1, p3, "Products with different IDs should not be equal");
  }
}
  */
