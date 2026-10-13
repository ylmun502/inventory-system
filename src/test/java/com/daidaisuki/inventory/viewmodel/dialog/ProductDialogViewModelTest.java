package com.daidaisuki.inventory.viewmodel.dialog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.model.builder.ProductBuilder;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class ProductDialogViewModelTest {
  @Test
  void newProductShouldStartInvalid() {
    ProductDialogViewModel viewModel = new ProductDialogViewModel(null, null);
    assertTrue(viewModel.isNewProperty().get());
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void requiredFieldsShouldMakeProductValid() {
    ProductDialogViewModel viewModel = new ProductDialogViewModel(null, null);
    viewModel.sku.set("PRD-001");
    viewModel.name.set("Test Product");
    viewModel.category.set("Plushy");
    viewModel.unitType.set("each");
    viewModel.weight.set("100");
    viewModel.price.set("39.99");
    assertFalse(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankSkuShouldBeInvalid() {
    ProductDialogViewModel viewModel = new ProductDialogViewModel(null, null);
    viewModel.sku.set("   ");
    viewModel.name.set("Test Product");
    viewModel.category.set("Plushy");
    viewModel.unitType.set("each");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankNameShouldBeInvalid() {
    ProductDialogViewModel viewModel = new ProductDialogViewModel(null, null);
    viewModel.sku.set("PRD-001");
    viewModel.name.set("   ");
    viewModel.category.set("Plushy");
    viewModel.unitType.set("each");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankCategoryShouldBeInvalid() {
    ProductDialogViewModel viewModel = new ProductDialogViewModel(null, null);
    viewModel.sku.set("PRD-001");
    viewModel.name.set("Test Product");
    viewModel.category.set("   ");
    viewModel.unitType.set("each");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankUnitTypeShouldBeInvalid() {
    ProductDialogViewModel viewModel = new ProductDialogViewModel(null, null);
    viewModel.sku.set("PRD-001");
    viewModel.name.set("Test Product");
    viewModel.category.set("Plushy");
    viewModel.unitType.set("   ");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void nonNumericWeightShouldBeInvalid() {
    ProductDialogViewModel viewModel = createValidNewProductViewModel();
    viewModel.weight.set("abc");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void nonNumericPriceShouldBeInvalid() {
    ProductDialogViewModel viewModel = createValidNewProductViewModel();
    viewModel.price.set("abc");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void numericWeightAndPriceShouldBeValid() {
    ProductDialogViewModel viewModel = createValidNewProductViewModel();
    viewModel.weight.set("80");
    viewModel.price.set("88.99");
    assertFalse(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankWeightAndPriceShouldBeInvalid() {
    ProductDialogViewModel viewModel = createValidNewProductViewModel();
    viewModel.weight.set("");
    viewModel.price.set("   ");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void createResultShouldNormalizeInput() {
    ProductDialogViewModel viewModel = new ProductDialogViewModel(null, null);
    viewModel.sku.set("   PRD-001   ");
    viewModel.name.set("   Test Product   ");
    viewModel.category.set("   Plushy   ");
    viewModel.description.set("   Winter Collection 2026   ");
    viewModel.weight.set("   80   ");
    viewModel.price.set("   88.99   ");
    viewModel.unitType.set("   each   ");
    viewModel.isActive.set(true);
    Product result = viewModel.createResult();
    assertEquals("PRD-001", result.getSku());
    assertEquals("Test Product", result.getName());
    assertEquals("Plushy", result.getCategory());
    assertEquals("Winter Collection 2026", result.getDescription());
    assertEquals(80, result.getWeight());
    assertEquals(new BigDecimal("88.99"), result.getSellingPrice());
    assertEquals("each", result.getUnitType());
    assertTrue(result.isActive());
  }

  @Test
  void blankOptionalValuesShouldUseExpectedDefaults() {
    ProductDialogViewModel viewModel = createValidNewProductViewModel();
    viewModel.description.set("   ");
    viewModel.weight.set("");
    viewModel.price.set("");
    Product result = viewModel.createResult();
    assertNull(result.getDescription());
    assertEquals(0, result.getWeight());
    assertEquals(BigDecimal.ZERO, result.getSellingPrice());
  }

  @Test
  void editShouldPopulatePropertiesFromProduct() {
    Product product =
        new ProductBuilder()
            .withSku("EDIT-001")
            .withName("Existing Product")
            .withCategory("Pin")
            .withDescription("Existing description")
            .withWeight(100)
            .withSellingPrice(new BigDecimal("99.99"))
            .withUnitType("piece")
            .inactive()
            .build();
    ProductDialogViewModel viewModel = new ProductDialogViewModel(null, product);
    assertFalse(viewModel.isNewProperty().get());
    assertFalse(viewModel.isInvalidProperty().get());
    assertEquals("EDIT-001", viewModel.sku.get());
    assertEquals("Existing Product", viewModel.name.get());
    assertEquals("Pin", viewModel.category.get());
    assertEquals("Existing description", viewModel.description.get());
    assertEquals("100", viewModel.weight.get());
    assertEquals("99.99", viewModel.price.get());
    assertEquals("piece", viewModel.unitType.get());
    assertFalse(viewModel.isActive.get());
  }

  @Test
  void editShouldReturnCopyWithoutMutatingOriginal() {
    Product original =
        new ProductBuilder()
            .withId(10)
            .withSku("OLD-001")
            .withName("Original Product")
            .withCategory("Original Category")
            .withSellingPrice(new BigDecimal("68.99"))
            .build();
    ProductDialogViewModel viewModel = new ProductDialogViewModel(null, original);
    viewModel.sku.set("NEW-001");
    viewModel.name.set("Updated Product");
    viewModel.category.set("Updated Category");
    viewModel.price.set("78.99");
    Product result = viewModel.createResult();
    assertNotSame(original, result);
    assertEquals("OLD-001", original.getSku());
    assertEquals("Original Product", original.getName());
    assertEquals("Original Category", original.getCategory());
    assertEquals(new BigDecimal("68.99"), original.getSellingPrice());
    assertEquals("NEW-001", result.getSku());
    assertEquals("Updated Product", result.getName());
    assertEquals("Updated Category", result.getCategory());
    assertEquals(new BigDecimal("78.99"), result.getSellingPrice());
  }

  @Test
  void editShouldPreserveInventoryAndPersistentFields() {
    OffsetDateTime createdAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
    OffsetDateTime updatedAt = OffsetDateTime.parse("2025-02-02T08:00:00Z");
    Product original =
        new ProductBuilder()
            .withId(25)
            .withBarcode("BAR-999")
            .withTaxCategory("Taxable")
            .withCurrentStock(88)
            .withMinStockLevel(20)
            .withMaxStockLevel(200)
            .withReorderingLevel(30)
            .withAverageUnitCost(new BigDecimal("18.75"))
            .withCreatedAt(createdAt)
            .withUpdatedAt(updatedAt)
            .deleted()
            .build();
    ProductDialogViewModel viewModel = new ProductDialogViewModel(null, original);
    viewModel.name.set("Updated Product");
    Product result = viewModel.createResult();
    assertEquals(25, result.getId());
    assertEquals("BAR-999", result.getBarcode());
    assertEquals("Taxable", result.getTaxCategory());
    assertEquals(88, result.getCurrentStock());
    assertEquals(20, result.getMinStockLevel());
    assertEquals(200, result.getMaxStockLevel());
    assertEquals(30, result.getReorderingLevel());
    assertEquals(new BigDecimal("18.75"), result.getAverageUnitCost());
    assertEquals(createdAt, result.getCreatedAt());
    assertEquals(updatedAt, result.getUpdatedAt());
    assertTrue(result.isDeleted());
  }

  private ProductDialogViewModel createValidNewProductViewModel() {
    ProductDialogViewModel viewModel = new ProductDialogViewModel(null, null);
    viewModel.sku.set("PRD-001");
    viewModel.name.set("Test Product");
    viewModel.category.set("Plushy");
    viewModel.unitType.set("each");
    viewModel.weight.set("100");
    viewModel.price.set("39.99");
    return viewModel;
  }
}
