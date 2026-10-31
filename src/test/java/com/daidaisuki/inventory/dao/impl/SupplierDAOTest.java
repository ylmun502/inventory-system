package com.daidaisuki.inventory.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.model.builder.SupplierBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SupplierDAOTest {
  private Connection connection;
  private SupplierDAO supplierDAO;

  @BeforeEach
  void setUp() throws SQLException {
    this.connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    this.resetSchema();
    this.supplierDAO = new SupplierDAO(this.connection);
  }

  @AfterEach
  void tearDown() throws SQLException {
    if (this.connection != null && !this.connection.isClosed()) {
      this.connection.close();
    }
  }

  @Test
  void saveShouldPersistSupplier() {
    Supplier supplier =
        new SupplierBuilder()
            .withName("New Supplier")
            .withShortCode("NEW01")
            .withEmail("new@example.com")
            .withPhone("1234567890")
            .withAddress("123 Main St")
            .build();
    Supplier saved = this.supplierDAO.save(supplier);
    assertTrue(saved.getId() > 0);
    assertEquals("New Supplier", saved.getName());
    assertEquals("NEW01", saved.getShortCode());
    assertEquals("new@example.com", saved.getEmail());
    assertEquals("1234567890", saved.getPhone());
    assertEquals("123 Main St", saved.getAddress());
    assertFalse(saved.isDeleted());
    Supplier persisted = this.supplierDAO.findById(saved.getId()).orElseThrow();
    assertEquals(saved.getId(), persisted.getId());
    assertEquals("New Supplier", persisted.getName());
  }

  @Test
  void findByIdShouldReturnExistingSupplier() {
    Supplier saved =
        this.supplierDAO.save(
            new SupplierBuilder().withName("Existing Supplier").withShortCode("EX01").build());
    Supplier result = this.supplierDAO.findById(saved.getId()).orElseThrow();
    assertEquals(saved.getId(), result.getId());
    assertEquals("Existing Supplier", result.getName());
    assertEquals("EX01", result.getShortCode());
  }

  @Test
  void findByIdShouldReturnEmptyWhenSupplierDoesNotExist() {
    assertTrue(this.supplierDAO.findById(9999).isEmpty());
  }

  @Test
  void findAllShouldReturnAllSuppliersOrderedByName() {
    this.supplierDAO.save(
        new SupplierBuilder().withName("Zulu Supplier").withShortCode("ZULU").build());
    this.supplierDAO.save(
        new SupplierBuilder().withName("Alpha Supplier").withShortCode("ALPHA").build());
    List<Supplier> suppliers = this.supplierDAO.findAll();
    assertEquals(2, suppliers.size());
    assertEquals("Alpha Supplier", suppliers.get(0).getName());
    assertEquals("Zulu Supplier", suppliers.get(1).getName());
  }

  @Test
  void updateShouldPersistEditableFields() {
    Supplier saved =
        this.supplierDAO.save(
            new SupplierBuilder()
                .withName("Original Supplier")
                .withShortCode("OLD01")
                .withEmail("old@example.com")
                .withPhone("1111111111")
                .withAddress("Old Address")
                .build());
    Supplier updated =
        new SupplierBuilder()
            .withId(saved.getId())
            .withName("Updated Supplier")
            .withShortCode("NEW01")
            .withEmail("new@example.com")
            .withPhone("2222222222")
            .withAddress("New Address")
            .build();
    this.supplierDAO.update(updated);
    Supplier result = this.supplierDAO.findById(saved.getId()).orElseThrow();
    assertEquals("Updated Supplier", result.getName());
    assertEquals("NEW01", result.getShortCode());
    assertEquals("new@example.com", result.getEmail());
    assertEquals("2222222222", result.getPhone());
    assertEquals("New Address", result.getAddress());
  }

  @Test
  void archiveShouldMarkSupplierDeleted() {
    Supplier saved =
        this.supplierDAO.save(
            new SupplierBuilder().withName("Archive Supplier").withShortCode("ARC01").build());
    this.supplierDAO.archive(saved.getId());
    Supplier archived = this.supplierDAO.findById(saved.getId()).orElseThrow();
    assertTrue(archived.isDeleted());
  }

  @Test
  void findByIdShouldIncludeArchivedSupplier() {
    Supplier saved =
        this.supplierDAO.save(
            new SupplierBuilder().withName("Archive Supplier").withShortCode("ARC01").build());
    this.supplierDAO.archive(saved.getId());
    Optional<Supplier> result = this.supplierDAO.findById(saved.getId());
    assertTrue(result.isPresent());
    assertTrue(result.orElseThrow().isDeleted());
  }

  @Test
  void restoreShouldMakeArchivedSupplierActiveAgain() {
    Supplier saved =
        this.supplierDAO.save(
            new SupplierBuilder().withName("Restore Supplier").withShortCode("RES01").build());
    this.supplierDAO.archive(saved.getId());
    this.supplierDAO.restore(saved.getId());
    Supplier restored = this.supplierDAO.findById(saved.getId()).orElseThrow();
    assertFalse(restored.isDeleted());
  }

  @Test
  void removeShouldPermanentlyDeleteSupplier() {
    Supplier saved =
        this.supplierDAO.save(
            new SupplierBuilder().withName("Delete Supplier").withShortCode("DEL01").build());
    this.supplierDAO.remove(saved.getId());
    assertTrue(this.supplierDAO.findById(saved.getId()).isEmpty());
  }

  @Test
  void existsByShortCodeShouldReturnTrueWhenShortCodeExists() {
    Supplier saved = this.supplierDAO.save(new SupplierBuilder().withShortCode("SUP01").build());
    assertTrue(this.supplierDAO.existsByShortCode("SUP01"));
  }

  @Test
  void existsByShortCodeShouldReturnFalseWhenShortCodeDoesNotExist() {
    assertFalse(this.supplierDAO.existsByShortCode("MISSING"));
  }

  @Test
  void existsByShortCodeShouldIncludeArchivedSupplier() {
    Supplier saved = this.supplierDAO.save(new SupplierBuilder().withShortCode("ARC01").build());
    this.supplierDAO.archive(saved.getId());
    assertTrue(this.supplierDAO.existsByShortCode("ARC01"));
  }

  @Test
  void existsByShortCodeExcludingIdShouldIgnoreSameSupplier() {
    Supplier saved = this.supplierDAO.save(new SupplierBuilder().withShortCode("SUP01").build());
    assertFalse(this.supplierDAO.existsByShortCodeExcludingId("SUP01", saved.getId()));
  }

  @Test
  void existsByShortCodeExcludingIdShouldDetectDifferentSupplier() {
    Supplier first =
        this.supplierDAO.save(
            new SupplierBuilder().withName("Supplier One").withShortCode("SUP01").build());
    Supplier second =
        this.supplierDAO.save(
            new SupplierBuilder().withName("Supplier Two").withShortCode("SUP02").build());
    assertTrue(this.supplierDAO.existsByShortCodeExcludingId(first.getShortCode(), second.getId()));
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
