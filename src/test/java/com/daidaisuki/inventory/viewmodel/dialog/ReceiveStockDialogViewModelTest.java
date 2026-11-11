package com.daidaisuki.inventory.viewmodel.dialog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.model.builder.ProductBuilder;
import com.daidaisuki.inventory.model.builder.SupplierBuilder;
import com.daidaisuki.inventory.model.dto.StockReceiveRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReceiveStockDialogViewModelTest {

  private Product product;
  private Supplier supplier;

  @BeforeEach
  void setUp() {
    this.product = new ProductBuilder().withId(10).withName("Test Product").build();

    this.supplier =
        new SupplierBuilder().withId(20).withName("Test Supplier").withShortCode("SUP").build();
  }

  @Test
  void validInputsShouldBeValid() {
    ReceiveStockDialogViewModel viewModel = this.createValidViewModel();
    assertFalse(viewModel.isInvalidProperty().get());
  }

  @Test
  void missingSupplierShouldBeInvalid() {
    ReceiveStockDialogViewModel viewModel =
        new ReceiveStockDialogViewModel(this.product, List.of());
    viewModel.batchCodeProperty().set("BCH-001");
    viewModel.quantityProperty().set("10");
    viewModel.unitCostProperty().set("8.00");
    viewModel.reasonProperty().set("Initial stock");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void supplierWithZeroIdShouldBeInvalid() {
    Supplier invalidSupplier =
        new SupplierBuilder().withId(0).withName("Invalid Supplier").withShortCode("NONE").build();
    ReceiveStockDialogViewModel viewModel =
        new ReceiveStockDialogViewModel(this.product, List.of(invalidSupplier));
    viewModel.batchCodeProperty().set("BCH-001");
    viewModel.quantityProperty().set("10");
    viewModel.unitCostProperty().set("8.00");
    viewModel.reasonProperty().set("Initial stock");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankBatchCodeShouldBeInvalid() {
    ReceiveStockDialogViewModel viewModel = this.createValidViewModel();
    viewModel.batchCodeProperty().set("");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void nonNumericQuantityShouldBeInvalid() {
    ReceiveStockDialogViewModel viewModel = this.createValidViewModel();
    viewModel.quantityProperty().set("abc");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void nonNumericUnitCostShouldBeInvalid() {
    ReceiveStockDialogViewModel viewModel = this.createValidViewModel();
    viewModel.unitCostProperty().set("abc");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void negativeQuantityShouldBeInvalid() {
    ReceiveStockDialogViewModel viewModel = this.createValidViewModel();
    viewModel.quantityProperty().set("-5");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void negativeUnitCostShouldBeInvalid() {
    ReceiveStockDialogViewModel viewModel = this.createValidViewModel();
    viewModel.unitCostProperty().set("-1.00");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void zeroUnitCostShouldBeValid() {
    ReceiveStockDialogViewModel viewModel = this.createValidViewModel();
    viewModel.unitCostProperty().set("0");
    assertFalse(viewModel.isInvalidProperty().get());
  }

  @Test
  void createResultShouldMapFields() {
    ReceiveStockDialogViewModel viewModel = this.createValidViewModel();
    viewModel.batchCodeProperty().set("BCH-001");
    viewModel.quantityProperty().set("25");
    viewModel.unitCostProperty().set("12.75");
    viewModel.expiryDateProperty().set(LocalDate.of(2027, 12, 31));
    viewModel.reasonProperty().set("Supplier delivery");
    StockReceiveRequest result = viewModel.createResult();
    assertEquals(this.product.getId(), result.productId());
    assertEquals(this.supplier.getId(), result.supplierId());
    assertEquals("BCH-001", result.batchCode());
    assertEquals(25, result.quantity());
    assertEquals(0, result.unitCost().compareTo(new BigDecimal("12.75")));
    assertEquals(OffsetDateTime.parse("2027-12-31T00:00:00Z"), result.expiryDate());
    assertEquals("Supplier delivery", result.reason());
  }

  @Test
  void nullExpiryDateShouldBeAllowed() {
    ReceiveStockDialogViewModel viewModel = this.createValidViewModel();
    viewModel.expiryDateProperty().set(null);

    assertFalse(viewModel.isInvalidProperty().get());
    StockReceiveRequest result = viewModel.createResult();
    assertNull(result.expiryDate());
  }

  @Test
  void zeroQuantityShouldBeInvalid() {
    ReceiveStockDialogViewModel viewModel = this.createValidViewModel();
    viewModel.quantityProperty().set("0");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankReasonShouldBeInvalid() {
    ReceiveStockDialogViewModel viewModel = this.createValidViewModel();
    viewModel.reasonProperty().set("");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void whitespaceOnlyReasonShouldBeInvalid() {
    ReceiveStockDialogViewModel viewModel = this.createValidViewModel();
    viewModel.reasonProperty().set("   ");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  private ReceiveStockDialogViewModel createValidViewModel() {
    ReceiveStockDialogViewModel viewModel =
        new ReceiveStockDialogViewModel(this.product, List.of(this.supplier));
    viewModel.batchCodeProperty().set("BCH-001");
    viewModel.quantityProperty().set("10");
    viewModel.unitCostProperty().set("5.00");
    viewModel.reasonProperty().set("Initial stock");
    return viewModel;
  }
}
