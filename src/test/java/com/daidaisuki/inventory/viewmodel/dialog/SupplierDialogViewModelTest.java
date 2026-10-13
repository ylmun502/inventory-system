package com.daidaisuki.inventory.viewmodel.dialog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.model.builder.SupplierBuilder;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class SupplierDialogViewModelTest {
  @Test
  void newSupplierShouldStartInvalid() {
    SupplierDialogViewModel viewModel = new SupplierDialogViewModel(null);
    assertTrue(viewModel.isNewProperty().get());
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void requiredFieldsShouldMakeSupplierValid() {
    SupplierDialogViewModel viewModel = new SupplierDialogViewModel(null);
    viewModel.name.set("Supplier One");
    viewModel.shortCode.set("SUP01");
    assertFalse(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankNameShouldBeInvalid() {
    SupplierDialogViewModel viewModel = new SupplierDialogViewModel(null);
    viewModel.name.set("   ");
    viewModel.shortCode.set("SUP01");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankShortCodeShouldBeInvalid() {
    SupplierDialogViewModel viewModel = new SupplierDialogViewModel(null);
    viewModel.name.set("Supplier One");
    viewModel.shortCode.set("   ");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void creeateResultShouldNormalizeInput() {
    SupplierDialogViewModel viewModel = new SupplierDialogViewModel(null);
    viewModel.name.set("  Supplier One  ");
    viewModel.shortCode.set("  SUP01  ");
    viewModel.email.set("  supplier@example.com  ");
    viewModel.phone.set("  1234567890  ");
    viewModel.address.set("  123 Main St  ");
    Supplier result = viewModel.createResult();
    assertEquals("Supplier One", result.getName());
    assertEquals("SUP01", result.getShortCode());
    assertEquals("supplier@example.com", result.getEmail());
    assertEquals("1234567890", result.getPhone());
    assertEquals("123 Main St", result.getAddress());
  }

  @Test
  void blankOptionalFieldsShouldBecomeNull() {
    SupplierDialogViewModel viewModel = new SupplierDialogViewModel(null);
    viewModel.name.set("Supplier One");
    viewModel.shortCode.set("SUP01");
    viewModel.email.set("   ");
    viewModel.phone.set("");
    viewModel.address.set("   ");
    Supplier result = viewModel.createResult();
    assertNull(result.getEmail());
    assertNull(result.getPhone());
    assertNull(result.getAddress());
  }

  @Test
  void editShouldPopulatePropertiesFromSupplier() {
    Supplier supplier =
        new SupplierBuilder()
            .withName("Existing Supplier")
            .withShortCode("EX01")
            .withEmail("existing@example.com")
            .withPhone("9876543210")
            .withAddress("456 Market St")
            .build();
    SupplierDialogViewModel viewModel = new SupplierDialogViewModel(supplier);
    assertFalse(viewModel.isNewProperty().get());
    assertFalse(viewModel.isInvalidProperty().get());
    assertEquals("Existing Supplier", viewModel.name.get());
    assertEquals("EX01", viewModel.shortCode.get());
    assertEquals("existing@example.com", viewModel.email.get());
    assertEquals("9876543210", viewModel.phone.get());
    assertEquals("456 Market St", viewModel.address.get());
  }

  @Test
  void editShouldReturnCopyWithoutMutatingOriginal() {
    Supplier original =
        new SupplierBuilder()
            .withId(10)
            .withName("Original Supplier")
            .withShortCode("OLD01")
            .withEmail("old@example.com")
            .build();
    SupplierDialogViewModel viewModel = new SupplierDialogViewModel(original);
    viewModel.name.set("Updated Supplier");
    viewModel.shortCode.set("NEW01");
    viewModel.email.set("new@example.com");
    Supplier result = viewModel.createResult();
    assertNotSame(original, result);
    assertEquals("Original Supplier", original.getName());
    assertEquals("OLD01", original.getShortCode());
    assertEquals("old@example.com", original.getEmail());
    assertEquals("Updated Supplier", result.getName());
    assertEquals("NEW01", result.getShortCode());
    assertEquals("new@example.com", result.getEmail());
  }

  @Test
  void editShouldPreservePersistentFields() {
    OffsetDateTime createdAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
    OffsetDateTime updatedAt = OffsetDateTime.parse("2025-02-02T08:00:00Z");
    Supplier original =
        new SupplierBuilder()
            .withId(25)
            .withCreatedAt(createdAt)
            .withUpdatedAt(updatedAt)
            .deleted()
            .build();
    SupplierDialogViewModel viewModel = new SupplierDialogViewModel(original);
    viewModel.name.set("Updated Supplier");
    Supplier result = viewModel.createResult();
    assertEquals(25, result.getId());
    assertEquals(createdAt, result.getCreatedAt());
    assertEquals(updatedAt, result.getUpdatedAt());
    assertTrue(result.isDeleted());
  }
}
