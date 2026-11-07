package com.daidaisuki.inventory.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.model.builder.ProductBuilder;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductDAOTest {
  private Connection connection;
  private ProductDAO productDAO;

  @BeforeEach
  void setUp() throws SQLException {
    this.connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    this.resetSchema();
    this.productDAO = new ProductDAO(this.connection);
  }

  @AfterEach
  void tearDown() throws SQLException {
    if (this.connection != null && !this.connection.isClosed()) {
      this.connection.close();
    }
  }

  @Test
  void saveShouldPersistProduct() {
    Product product =
        new ProductBuilder()
            .withSku("PRD-001")
            .withBarcode("BAR-001")
            .withName("Test Product")
            .withCategory("Plushy")
            .withUnitType("each")
            .withTaxCategory("Taxable")
            .withDescription("Test description")
            .withWeight(new BigDecimal("10.225"))
            .withMinStockLevel(5)
            .withMaxStockLevel(100)
            .withReorderingLevel(10)
            .withSellingPrice(new BigDecimal("39.99"))
            .withAverageUnitCost(new BigDecimal("12.5"))
            .build();
    Product saved = this.productDAO.save(product);
    assertTrue(saved.getId() > 0);
    assertEquals("PRD-001", saved.getSku());
    assertEquals("BAR-001", saved.getBarcode());
    assertEquals("Test Product", saved.getName());
    assertEquals("Plushy", saved.getCategory());
    assertEquals("each", saved.getUnitType());
    assertEquals("Taxable", saved.getTaxCategory());
    assertEquals("Test description", saved.getDescription());
    assertEquals(0, saved.getWeight().compareTo(new BigDecimal("10.225")));
    assertEquals(0, saved.getSellingPrice().compareTo(new BigDecimal("39.99")));
    assertEquals(0, saved.getAverageUnitCost().compareTo(new BigDecimal("12.5")));
    assertEquals(0, saved.getCurrentStock());
    assertTrue(saved.isActive());
    assertFalse(saved.isDeleted());
    Product persisted = this.productDAO.findById(saved.getId()).orElseThrow();
    assertEquals(saved.getId(), persisted.getId());
    assertEquals(saved.getSku(), persisted.getSku());
    assertEquals(saved.getBarcode(), persisted.getBarcode());
    assertEquals(saved.getName(), persisted.getName());
    assertEquals(saved.getCategory(), persisted.getCategory());
    assertEquals(saved.getUnitType(), persisted.getUnitType());
    assertEquals(saved.getTaxCategory(), persisted.getTaxCategory());
    assertEquals(saved.getDescription(), persisted.getDescription());
    assertEquals(0, persisted.getWeight().compareTo(saved.getWeight()));
    assertEquals(saved.getCurrentStock(), persisted.getCurrentStock());
    assertEquals(saved.getMinStockLevel(), persisted.getMinStockLevel());
    assertEquals(saved.getMaxStockLevel(), persisted.getMaxStockLevel());
    assertEquals(saved.getReorderingLevel(), persisted.getReorderingLevel());
    assertEquals(0, persisted.getSellingPrice().compareTo(saved.getSellingPrice()));
    assertEquals(0, persisted.getAverageUnitCost().compareTo(saved.getAverageUnitCost()));
    assertEquals(saved.isActive(), persisted.isActive());
    assertEquals(saved.getCreatedAt(), persisted.getCreatedAt());
    assertEquals(saved.getUpdatedAt(), persisted.getUpdatedAt());
    assertEquals(saved.isDeleted(), persisted.isDeleted());
  }

  @Test
  void saveShouldInitializeCurrentStockToZero() {
    Product product = new ProductBuilder().withCurrentStock(999).build();
    Product saved = this.productDAO.save(product);
    assertEquals(0, saved.getCurrentStock());
    Product persisted = this.productDAO.findById(saved.getId()).orElseThrow();
    assertEquals(0, persisted.getCurrentStock());
  }

  @Test
  void findByIdShouldReturnExistingProduct() {
    Product saved = this.productDAO.save(new ProductBuilder().withName("Existing Product").build());
    Product result = this.productDAO.findById(saved.getId()).orElseThrow();
    assertEquals(saved.getId(), result.getId());
    assertEquals("Existing Product", result.getName());
  }

  @Test
  void findByIdShouldReturnEmptyWhenProductDoesNotExist() {
    assertTrue(this.productDAO.findById(9999).isEmpty());
  }

  @Test
  void findAllShouldReturnAllProductsOrderedByName() {
    this.productDAO.save(
        new ProductBuilder()
            .withSku("ZULU-001")
            .withBarcode("ZULUBAR")
            .withName("Zulu Product")
            .build());
    this.productDAO.save(
        new ProductBuilder()
            .withSku("ALPHA-001")
            .withBarcode("ALPHABAR")
            .withName("Alpha Product")
            .build());
    List<Product> products = this.productDAO.findAll();
    assertEquals(2, products.size());
    assertEquals("Alpha Product", products.get(0).getName());
    assertEquals("Zulu Product", products.get(1).getName());
  }

  @Test
  void updateShouldPersistEditableFields() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder()
                .withSku("OLD-001")
                .withBarcode("OLD-BAR")
                .withName("Original Product")
                .build());
    Product updated =
        new ProductBuilder()
            .withId(saved.getId())
            .withSku("NEW-001")
            .withBarcode("NEW-BAR")
            .withName("Updated Product")
            .withCategory("Updated Category")
            .withUnitType("each")
            .withTaxCategory("Taxable")
            .withDescription("Updated description")
            .withWeight(new BigDecimal("250.65"))
            .withMinStockLevel(15)
            .withMaxStockLevel(200)
            .withReorderingLevel(25)
            .withSellingPrice(new BigDecimal("89.99"))
            .inactive()
            .build();
    this.productDAO.update(updated);
    Product result = this.productDAO.findById(saved.getId()).orElseThrow();
    assertEquals("NEW-001", result.getSku());
    assertEquals("NEW-BAR", result.getBarcode());
    assertEquals("Updated Product", result.getName());
    assertEquals("Updated Category", result.getCategory());
    assertEquals("each", result.getUnitType());
    assertEquals("Taxable", result.getTaxCategory());
    assertEquals("Updated description", result.getDescription());
    assertEquals(0, result.getWeight().compareTo(new BigDecimal("250.65")));
    assertEquals(15, result.getMinStockLevel());
    assertEquals(200, result.getMaxStockLevel());
    assertEquals(25, result.getReorderingLevel());
    assertEquals(0, result.getSellingPrice().compareTo(new BigDecimal("89.99")));
    assertTrue(saved.isActive());
  }

  @Test
  void updateShouldPreserveInventoryControlledFields() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());
    assertTrue(this.productDAO.updateStockTotalAndCost(saved.getId(), 30, new BigDecimal("12.75")));
    Product beforeUpdate = this.productDAO.findById(saved.getId()).orElseThrow();
    assertEquals(30, beforeUpdate.getCurrentStock());
    Product metadataUpdate =
        new ProductBuilder()
            .withId(saved.getId())
            .withSku(beforeUpdate.getSku())
            .withBarcode(beforeUpdate.getBarcode())
            .withName("Metadata Updated")
            .withCurrentStock(999)
            .withAverageUnitCost(new BigDecimal("999.99"))
            .build();
    this.productDAO.update(metadataUpdate);
    Product result = this.productDAO.findById(saved.getId()).orElseThrow();
    assertEquals("Metadata Updated", result.getName());
    assertEquals(30, result.getCurrentStock());
    assertEquals(0, result.getAverageUnitCost().compareTo(new BigDecimal("12.75")));
  }

  @Test
  void archiveShouldMarkProductDeleted() {
    Product saved = this.productDAO.save(new ProductBuilder().withName("Archive Product").build());
    this.productDAO.archive(saved.getId());
    Product archived = this.productDAO.findById(saved.getId()).orElseThrow();
    assertTrue(archived.isDeleted());
  }

  @Test
  void findByIdShouldIncludeArchivedProduct() {
    Product saved = this.productDAO.save(new ProductBuilder().withName("Archive Product").build());
    this.productDAO.archive(saved.getId());
    Optional<Product> result = this.productDAO.findById(saved.getId());
    assertTrue(result.isPresent());
    assertTrue(result.orElseThrow().isDeleted());
  }

  @Test
  void restoreShouldMakeArchivedProductActiveAgain() {
    Product saved = this.productDAO.save(new ProductBuilder().withName("Restore Product").build());
    this.productDAO.archive(saved.getId());
    this.productDAO.restore(saved.getId());
    Product restored = this.productDAO.findById(saved.getId()).orElseThrow();
    assertFalse(restored.isDeleted());
  }

  @Test
  void removeShouldPermanentlyDeleteProduct() {
    Product saved = this.productDAO.save(new ProductBuilder().withName("Delete Product").build());
    this.productDAO.remove(saved.getId());
    assertTrue(this.productDAO.findById(saved.getId()).isEmpty());
  }

  @Test
  void existsShouldReturnTrueForActiveProduct() {
    Product saved = this.productDAO.save(new ProductBuilder().build());
    assertTrue(this.productDAO.exists(saved.getId()));
  }

  @Test
  void existsShouldReturnFalseWhenProductDoesNotExist() {
    assertFalse(this.productDAO.exists(9999));
  }

  @Test
  void existsBySkuShouldReturnTrueWhenSkuExists() {
    Product saved = this.productDAO.save(new ProductBuilder().withSku("SKU-001").build());
    assertTrue(this.productDAO.existsBySku("SKU-001"));
  }

  @Test
  void existsBySkuShouldReturnFalseWhenSKUDoesNotExist() {
    assertFalse(this.productDAO.existsBySku("MISSING"));
  }

  @Test
  void existsBySkuShouldIncludeArchivedProduct() {
    Product saved = this.productDAO.save(new ProductBuilder().withSku("ARC-001").build());
    this.productDAO.archive(saved.getId());
    assertTrue(this.productDAO.existsBySku("ARC-001"));
  }

  @Test
  void existsBySkuExcludingIdShouldIgnoreSameProduct() {
    Product saved = this.productDAO.save(new ProductBuilder().withSku("SKU-001").build());
    this.productDAO.archive(saved.getId());
    assertFalse(this.productDAO.existsBySkuExcludingId("SKU-001", saved.getId()));
  }

  @Test
  void existsBySkuExcludingIdShouldDetectDifferentProduct() {
    Product first =
        this.productDAO.save(
            new ProductBuilder().withSku("SKU-001").withBarcode("BAR-001").build());
    Product second =
        this.productDAO.save(
            new ProductBuilder().withSku("SKU-002").withBarcode("BAR-002").build());
    assertTrue(this.productDAO.existsBySkuExcludingId(first.getSku(), second.getId()));
  }

  @Test
  void existsByBarcodeShouldReturnTrueWhenBarcodeExists() {
    Product saved = this.productDAO.save(new ProductBuilder().withBarcode("BAR-001").build());
    assertTrue(this.productDAO.existsByBarcode("BAR-001"));
  }

  @Test
  void existsByBarcodeShouldReturnFalseWhenBarcodeDoesNotExist() {
    assertFalse(this.productDAO.existsByBarcode("MISSING"));
  }

  @Test
  void existsByBarcodeShouldReturnFalseForNullOrBlankBarcode() {
    assertFalse(this.productDAO.existsByBarcode(null));
    assertFalse(this.productDAO.existsByBarcode(""));
    assertFalse(this.productDAO.existsByBarcode("   "));
  }

  @Test
  void existsByBarcodeShouldIncludeArchivedProduct() {
    Product saved = this.productDAO.save(new ProductBuilder().withBarcode("ARC-001").build());
    this.productDAO.archive(saved.getId());
    assertTrue(this.productDAO.existsByBarcode("ARC-001"));
  }

  @Test
  void existsByBarcodeExcludingIdShouldIgnoreSameProduct() {
    Product saved = this.productDAO.save(new ProductBuilder().withBarcode("BAR-001").build());
    this.productDAO.archive(saved.getId());
    assertFalse(this.productDAO.existsBySkuExcludingId("BAR-001", saved.getId()));
  }

  @Test
  void existsByBarcodeExcludingIdShouldDetectDifferentProduct() {
    Product first =
        this.productDAO.save(
            new ProductBuilder().withSku("SKU-001").withBarcode("BAR-001").build());
    Product second =
        this.productDAO.save(
            new ProductBuilder().withSku("SKU-002").withBarcode("BAR-002").build());
    assertTrue(this.productDAO.existsByBarcodeExcludingId(first.getBarcode(), second.getId()));
  }

  @Test
  void updateStockTotalShouldIncreasedStock() {
    Product saved = this.productDAO.save(new ProductBuilder().build());
    assertTrue(this.productDAO.updateStockTotal(saved.getId(), 25));
    Product result = this.productDAO.findById(saved.getId()).orElseThrow();
    assertEquals(25, result.getCurrentStock());
  }

  @Test
  void updateStockTotalShouldDecreasedStock() {
    Product saved = this.productDAO.save(new ProductBuilder().build());
    assertTrue(this.productDAO.updateStockTotal(saved.getId(), 25));
    assertTrue(this.productDAO.updateStockTotal(saved.getId(), -10));
    Product result = this.productDAO.findById(saved.getId()).orElseThrow();
    assertEquals(15, result.getCurrentStock());
  }

  @Test
  void updateStockTotalShouldRejectNegativeResultingStock() {
    Product saved = this.productDAO.save(new ProductBuilder().build());
    assertTrue(this.productDAO.updateStockTotal(saved.getId(), 10));
    assertFalse(this.productDAO.updateStockTotal(saved.getId(), -11));
    Product result = this.productDAO.findById(saved.getId()).orElseThrow();
    assertEquals(10, result.getCurrentStock());
  }

  @Test
  void updateStockTotalShouldReturnFalseForArchivedProduct() {
    Product saved = this.productDAO.save(new ProductBuilder().build());
    this.productDAO.archive(saved.getId());
    assertFalse(this.productDAO.updateStockTotal(saved.getId(), 10));
  }

  @Test
  void updateStockTotalAndCostShouldUpdateBothFields() {
    Product saved = this.productDAO.save(new ProductBuilder().build());
    assertTrue(this.productDAO.updateStockTotalAndCost(saved.getId(), 20, new BigDecimal("13.25")));
    Product result = this.productDAO.findById(saved.getId()).orElseThrow();
    assertEquals(20, result.getCurrentStock());
    assertEquals(0, result.getAverageUnitCost().compareTo(new BigDecimal("13.25")));
  }

  @Test
  void updateStockTotalAndCostShouldRejectNegativeResultingStock() {
    Product saved = this.productDAO.save(new ProductBuilder().build());
    assertTrue(this.productDAO.updateStockTotalAndCost(saved.getId(), 10, new BigDecimal("12")));
    assertFalse(
        this.productDAO.updateStockTotalAndCost(saved.getId(), -11, new BigDecimal("99.99")));
    Product result = this.productDAO.findById(saved.getId()).orElseThrow();
    assertEquals(10, result.getCurrentStock());
    assertEquals(0, result.getAverageUnitCost().compareTo(new BigDecimal("12")));
  }

  @Test
  void findAllDistinctUnitTypesShouldReturnUniqueSortedValues() {
    this.productDAO.save(
        new ProductBuilder()
            .withSku("PRD-001")
            .withBarcode("BAR-001")
            .withUnitType("piece")
            .build());
    this.productDAO.save(
        new ProductBuilder().withSku("PRD-002").withBarcode("BAR-002").withUnitType("box").build());
    this.productDAO.save(
        new ProductBuilder()
            .withSku("PRD-003")
            .withBarcode("BAR-003")
            .withUnitType("each")
            .build());
    List<String> unitTypes = this.productDAO.findAllDistinctUnitTypes();
    assertEquals(List.of("box", "each", "piece"), unitTypes);
  }

  private void resetSchema() throws SQLException {
    try (Statement stmt = this.connection.createStatement()) {
      stmt.execute("DROP TABLE IF EXISTS products");
      String sql =
          """
          CREATE Table products (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              sku TEXT UNIQUE,
              barcode TEXT UNIQUE,
              name TEXT NOT NULL,
              category TEXT NOT NULL,
              unit_type TEXT DEFAULT 'each',
              tax_category TEXT DEFAULT 'standard',
              description TEXT,
              weight INTEGER NOT NULL,
              current_stock INTEGER DEFAULT 0 NOT NULL CHECK (current_stock >= 0),
              min_stock_level INTEGER DEFAULT 2,
              max_stock_level INTEGER DEFAULT 100,
              reordering_level INTEGER DEFAULT 5,
              selling_price_cents INTEGER NOT NULL,
              average_unit_cost_cents INTEGER NOT NULL,
              is_active INTEGER DEFAULT 1,
              created_at DATETIME NOT NULL,
              updated_at DATETIME NOT NULL,
              is_deleted INTEGER NOT NULL)
          """;
      stmt.execute(sql);
    }
  }
}
