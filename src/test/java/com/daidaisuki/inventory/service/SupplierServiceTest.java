package com.daidaisuki.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.model.builder.SupplierBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SupplierServiceTest {
  private Connection connection;
  private SupplierService supplierService;

  @BeforeEach
  void setUp() throws SQLException {
    this.connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    this.resetSchema();
    this.supplierService = new SupplierService(this.connection);
  }

  @AfterEach
  void tearDown() throws SQLException {
    if (this.connection != null && !this.connection.isClosed()) {
      this.connection.close();
    }
  }

  @Test
  void createSupplierShouldPersistSupplier() {
    Supplier supplier =
        new SupplierBuilder().withName("New Supplier").withShortCode("NEW01").build();
    this.supplierService.createSupplier(supplier);
    List<Supplier> suppliers = this.supplierService.listAll();
    assertEquals(1, suppliers.size());
    Supplier saved = suppliers.getFirst();
    assertEquals("New Supplier", saved.getName());
    assertEquals("NEW01", saved.getShortCode());
    assertFalse(saved.isDeleted());
  }

  @Test
  void createSupplierWithDuplicateShortCodeShouldThrow() {
    this.supplierService.createSupplier(
        new SupplierBuilder().withName("Supplier One").withShortCode("DUP01").build());
    Supplier duplicate =
        new SupplierBuilder().withName("Supplier Two").withShortCode("DUP01").build();
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> this.supplierService.createSupplier(duplicate));
    assertEquals("A supplier with this short code already exists.", exception.getMessage());
    assertEquals(1, this.supplierService.listAll().size());
  }

  @Test
  void archivedSupplierShouldStillReserveShortCode() {
    this.supplierService.createSupplier(
        new SupplierBuilder().withName("Supplier One").withShortCode("ARC01").build());
    Supplier saved = this.supplierService.listAll().getFirst();
    this.supplierService.archive(saved.getId());
    Supplier duplicate =
        new SupplierBuilder().withName("Supplier Two").withShortCode("ARC01").build();
    assertThrows(
        IllegalArgumentException.class, () -> this.supplierService.createSupplier(duplicate));
  }

  @Test
  void updateSupplierShouldPersistEditableFields() {
    this.supplierService.createSupplier(
        new SupplierBuilder().withName("Original Supplier").withShortCode("OLD01").build());
    Supplier saved = this.supplierService.listAll().getFirst();
    Supplier updated =
        new SupplierBuilder()
            .withId(saved.getId())
            .withName("Updated Supplier")
            .withShortCode("NEW01")
            .withEmail("updated@example.com")
            .build();
    this.supplierService.updateSupplier(updated);
    Supplier result =
        this.supplierService.listAll().stream()
            .filter(supplier -> supplier.getId() == saved.getId())
            .findFirst()
            .orElseThrow();
    assertEquals("Updated Supplier", result.getName());
    assertEquals("NEW01", result.getShortCode());
    assertEquals("updated@example.com", result.getEmail());
  }

  @Test
  void updateShouldAllowSupplierToKeepSameShortCode() {
    this.supplierService.createSupplier(
        new SupplierBuilder().withName("Supplier One").withShortCode("SUP01").build());
    Supplier saved = this.supplierService.listAll().getFirst();
    Supplier updated =
        new SupplierBuilder()
            .withId(saved.getId())
            .withName("Renamed Supplier")
            .withShortCode("SUP01")
            .build();
    this.supplierService.updateSupplier(updated);
    Supplier result =
        this.supplierService.listAll().stream()
            .filter(supplier -> supplier.getId() == saved.getId())
            .findFirst()
            .orElseThrow();
    assertEquals("Renamed Supplier", result.getName());
    assertEquals("SUP01", result.getShortCode());
  }

  @Test
  void updateToAnotherSupplierShortCodeShouldThrow() {
    this.supplierService.createSupplier(
        new SupplierBuilder().withName("Supplier One").withShortCode("SUP01").build());
    this.supplierService.createSupplier(
        new SupplierBuilder().withName("Supplier Two").withShortCode("SUP02").build());
    List<Supplier> suppliers = this.supplierService.listAll();
    Supplier supplierTwo =
        suppliers.stream()
            .filter(supplier -> supplier.getShortCode().equals("SUP02"))
            .findFirst()
            .orElseThrow();
    Supplier conflictingUpdate =
        new SupplierBuilder()
            .withId(supplierTwo.getId())
            .withName("Supplier Two")
            .withShortCode("SUP01")
            .build();
    assertThrows(
        IllegalArgumentException.class,
        () -> this.supplierService.updateSupplier(conflictingUpdate));
    Supplier unchanged =
        this.supplierService.listAll().stream()
            .filter(supplier -> supplier.getId() == supplierTwo.getId())
            .findFirst()
            .orElseThrow();
    assertEquals("SUP02", unchanged.getShortCode());
  }

  @Test
  void archiveShouldMarkSupplierDeleted() {
    this.supplierService.createSupplier(new SupplierBuilder().withName("Archive Supplier").build());
    Supplier saved = this.supplierService.listAll().getFirst();
    this.supplierService.archive(saved.getId());
    List<Supplier> suppliers = this.supplierService.listAll();
    Supplier archived =
        this.supplierService.listAll().stream()
            .filter(supplier -> supplier.getId() == saved.getId())
            .findFirst()
            .orElseThrow();
    assertTrue(archived.isDeleted());
  }

  @Test
  void restoreShouldMakeArchivedSupplierActiveAgain() {
    this.supplierService.createSupplier(new SupplierBuilder().withName("Restore Supplier").build());
    Supplier saved = this.supplierService.listAll().getFirst();
    this.supplierService.archive(saved.getId());
    this.supplierService.restore(saved.getId());
    Supplier restored =
        this.supplierService.listAll().stream()
            .filter(supplier -> supplier.getId() == saved.getId())
            .findFirst()
            .orElseThrow();
    assertFalse(restored.isDeleted());
  }

  @Test
  void removeShouldPermanentlyDeleteSupplier() {
    this.supplierService.createSupplier(new SupplierBuilder().withName("Delete Supplier").build());
    Supplier saved = this.supplierService.listAll().getFirst();
    this.supplierService.archive(saved.getId());
    this.supplierService.remove(saved.getId());
    assertTrue(this.supplierService.listAll().isEmpty());
  }

  @Test
  void listAllShouldIncludeArchivedSuppliers() {
    this.supplierService.createSupplier(
        new SupplierBuilder().withName("Active Supplier").withShortCode("ACT01").build());
    this.supplierService.createSupplier(
        new SupplierBuilder().withName("Archived Supplier").withShortCode("ARC01").build());
    Supplier archived =
        this.supplierService.listAll().stream()
            .filter(supplier -> supplier.getName().equals("Archived Supplier"))
            .findFirst()
            .orElseThrow();
    this.supplierService.archive(archived.getId());
    List<Supplier> suppliers = this.supplierService.listAll();
    assertEquals(2, suppliers.size());
    assertTrue(suppliers.stream().anyMatch(Supplier::isDeleted));
  }

  private void resetSchema() throws SQLException {
    try (Statement stmt = this.connection.createStatement()) {
      stmt.execute("DROP TABLE IF EXISTS suppliers");
      String sql =
          """
          CREATE Table suppliers (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT NOT NULL,
              short_code TEXT NOT NULL UNIQUE,
              email TEXT,
              phone TEXT,
              address TEXT,
              created_at TEXT NOT NULL,
              updated_at TEXT NOT NULL,
              is_deleted INTEGER NOT NULL DEFAULT 0)
          """;
      stmt.execute(sql);
    }
  }
}
