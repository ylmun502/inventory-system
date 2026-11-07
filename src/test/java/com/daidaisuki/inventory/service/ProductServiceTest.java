package com.daidaisuki.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daidaisuki.inventory.dao.impl.ProductDAO;
import com.daidaisuki.inventory.exception.EntityNotFoundException;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.model.builder.ProductBuilder;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductServiceTest {
  private Connection connection;
  private ProductService productService;
  private ProductDAO productDAO;

  @BeforeEach
  void setUp() throws SQLException {
    this.connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    this.resetSchema();
    this.productService = new ProductService(this.connection);
    this.productDAO = new ProductDAO(this.connection);
  }

  @AfterEach
  void tearDown() throws SQLException {
    if (this.connection != null && !this.connection.isClosed()) {
      this.connection.close();
    }
  }

  @Test
  void createProductShouldPersistProduct() {
    Product product =
        new ProductBuilder()
            .withSku("PRD-001")
            .withBarcode("BAR-001")
            .withName("Test Product")
            .withCategory("Plushy")
            .withUnitType("each")
            .withTaxCategory("Taxable")
            .withWeight(new BigDecimal("12.75"))
            .withSellingPrice(new BigDecimal("39.99"))
            .build();
    this.productService.createProduct(product);
    List<Product> products = this.productService.listAll();
    assertEquals(1, products.size());
    Product saved = products.getFirst();
    assertEquals("PRD-001", saved.getSku());
    assertEquals("BAR-001", saved.getBarcode());
    assertEquals("Test Product", saved.getName());
    assertEquals("Plushy", saved.getCategory());
    assertEquals("each", saved.getUnitType());
    assertEquals("Taxable", saved.getTaxCategory());
    assertEquals(0, saved.getWeight().compareTo(new BigDecimal("12.75")));
    assertEquals(0, saved.getSellingPrice().compareTo(new BigDecimal("39.99")));
    assertEquals(0, saved.getCurrentStock());
    assertTrue(saved.isActive());
    assertFalse(saved.isDeleted());
  }

  @Test
  void createProductWithBlankBarcodeShouldGenerateBarcode() {
    Product product = new ProductBuilder().withSku("PRD-001").withBarcode(null).build();
    this.productService.createProduct(product);
    Product saved = this.productService.listAll().getFirst();
    assertNotNull(saved.getBarcode());
    assertFalse(saved.getBarcode().isBlank());
    assertEquals(8, saved.getBarcode().length());
  }

  @Test
  void createProductShouldPreserveSuppliedBarcode() {
    Product product = new ProductBuilder().withSku("PRD-001").withBarcode("CUSTOM01").build();
    this.productService.createProduct(product);
    Product saved = this.productService.listAll().getFirst();
    assertEquals("CUSTOM01", saved.getBarcode());
  }

  @Test
  void createProductWithDuplicateSkuShouldThrow() {
    this.productService.createProduct(
        new ProductBuilder().withSku("DUP-001").withBarcode("BAR-001").build());
    Product duplicate = new ProductBuilder().withSku("DUP-001").withBarcode("BAR-002").build();
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> this.productService.createProduct(duplicate));
    assertEquals("A product with this SKU already exists.", exception.getMessage());
    assertEquals(1, productService.listAll().size());
  }

  @Test
  void createProductWithDuplicateBarcodeShouldThrow() {
    this.productService.createProduct(
        new ProductBuilder().withSku("PRD-001").withBarcode("DUPBAR01").build());
    Product duplicate = new ProductBuilder().withSku("PRD-002").withBarcode("DUPBAR01").build();
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> this.productService.createProduct(duplicate));
    assertEquals("A product with this barcode already exists.", exception.getMessage());
  }

  @Test
  void archiveProductShouldStillReservedSkuAndBarcode() {
    this.productService.createProduct(
        new ProductBuilder().withSku("ARC-001").withBarcode("ARCBAR01").build());
    Product saved = this.productService.listAll().getFirst();
    this.productService.archive(saved.getId());
    Product duplicateSku = new ProductBuilder().withSku("ARC-001").withBarcode("NEWBAR01").build();
    assertThrows(
        IllegalArgumentException.class, () -> this.productService.createProduct(duplicateSku));
    Product duplicateBarcode =
        new ProductBuilder().withSku("New-001").withBarcode("ARCBAR01").build();
    assertThrows(
        IllegalArgumentException.class, () -> this.productService.createProduct(duplicateBarcode));
  }

  @Test
  void updateProductShouldPersistEditableFields() {
    this.productService.createProduct(
        new ProductBuilder()
            .withSku("OLD-001")
            .withBarcode("BAR-001")
            .withName("Original Product")
            .build());
    Product saved = this.productService.listAll().getFirst();
    Product updated =
        new ProductBuilder()
            .withId(saved.getId())
            .withSku("NEW-001")
            .withBarcode("BAR-002")
            .withName("Updated Product")
            .withCategory("Updated Category")
            .withUnitType("piece")
            .withTaxCategory("Taxable")
            .withDescription("Updated description")
            .withWeight(new BigDecimal("88.725"))
            .withMinStockLevel(10)
            .withMaxStockLevel(180)
            .withReorderingLevel(20)
            .withSellingPrice(new BigDecimal("89.99"))
            .inactive()
            .build();
    this.productService.updateProduct(updated);
    Product result = this.productService.getProduct(saved.getId());
    assertEquals("NEW-001", result.getSku());
    assertEquals("BAR-002", result.getBarcode());
    assertEquals("Updated Product", result.getName());
    assertEquals("Updated Category", result.getCategory());
    assertEquals("piece", result.getUnitType());
    assertEquals("Taxable", result.getTaxCategory());
    assertEquals("Updated description", result.getDescription());
    assertEquals(0, result.getWeight().compareTo(new BigDecimal("88.725")));
    assertEquals(10, result.getMinStockLevel());
    assertEquals(180, result.getMaxStockLevel());
    assertEquals(20, result.getReorderingLevel());
    assertEquals(0, result.getSellingPrice().compareTo(new BigDecimal("89.99")));
    assertFalse(result.isActive());
  }

  @Test
  void updateShouldAllowProductToKeepSameSkuAndBarcode() {
    this.productService.createProduct(
        new ProductBuilder()
            .withSku("PRD-001")
            .withBarcode("BAR-001")
            .withName("Original Product")
            .build());
    Product saved = this.productService.listAll().getFirst();
    Product updated =
        new ProductBuilder()
            .withId(saved.getId())
            .withSku("PRD-001")
            .withBarcode("BAR-001")
            .withName("Renamed Product")
            .build();
    this.productService.updateProduct(updated);
    Product result = this.productService.getProduct(saved.getId());
    assertEquals("PRD-001", result.getSku());
    assertEquals("BAR-001", result.getBarcode());
    assertEquals("Renamed Product", result.getName());
  }

  @Test
  void updateToAnotherProductsSkuShouldThrow() {
    this.productService.createProduct(
        new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());
    this.productService.createProduct(
        new ProductBuilder().withSku("PRD-002").withBarcode("BAR-002").build());
    Product productTwo =
        this.productService.listAll().stream()
            .filter(product -> product.getSku().equals("PRD-002"))
            .findFirst()
            .orElseThrow();
    Product conflictingUpdate =
        new ProductBuilder()
            .withId(productTwo.getId())
            .withSku("PRD-001")
            .withBarcode("BAR-002")
            .build();
    assertThrows(
        IllegalArgumentException.class, () -> this.productService.updateProduct(conflictingUpdate));
    Product unchanged = this.productService.getProduct(productTwo.getId());
    assertEquals("PRD-002", unchanged.getSku());
  }

  @Test
  void updateToAnotherProductsBarcodeShouldThrow() {
    this.productService.createProduct(
        new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());
    this.productService.createProduct(
        new ProductBuilder().withSku("PRD-002").withBarcode("BAR-002").build());
    Product productTwo =
        this.productService.listAll().stream()
            .filter(product -> product.getSku().equals("PRD-002"))
            .findFirst()
            .orElseThrow();
    Product conflictingUpdate =
        new ProductBuilder()
            .withId(productTwo.getId())
            .withSku("PRD-002")
            .withBarcode("BAR-001")
            .build();
    assertThrows(
        IllegalArgumentException.class, () -> this.productService.updateProduct(conflictingUpdate));
    Product unchanged = this.productService.getProduct(productTwo.getId());
    assertEquals("BAR-002", unchanged.getBarcode());
  }

  @Test
  void metadataUpdateShouldPreserveInventoryControlledFields() {
    this.productService.createProduct(
        new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());
    Product saved = this.productService.listAll().getFirst();
    boolean inventoryUpdated =
        this.productDAO.updateStockTotalAndCost(saved.getId(), 25, new BigDecimal("12.75"));
    assertTrue(inventoryUpdated);
    Product beforeEdit = this.productService.getProduct(saved.getId());
    assertEquals(25, beforeEdit.getCurrentStock());
    assertEquals(0, beforeEdit.getAverageUnitCost().compareTo(new BigDecimal("12.75")));
    Product metadataUpdate =
        new ProductBuilder()
            .withId(saved.getId())
            .withSku(beforeEdit.getSku())
            .withBarcode(beforeEdit.getBarcode())
            .withName("Metadata Updated")
            .withCurrentStock(999)
            .withAverageUnitCost(new BigDecimal("999.99"))
            .build();
    this.productService.updateProduct(metadataUpdate);
    Product result = this.productService.getProduct(saved.getId());
    assertEquals("Metadata Updated", result.getName());
    assertEquals(25, result.getCurrentStock());
    assertEquals(0, result.getAverageUnitCost().compareTo(new BigDecimal("12.75")));
  }

  @Test
  void archiveShouldMarkProductDeleted() {
    this.productService.createProduct(new ProductBuilder().withName("Archive Product").build());
    Product saved = this.productService.listAll().getFirst();
    this.productService.archive(saved.getId());
    Product archived = this.productService.getProduct(saved.getId());
    assertTrue(archived.isDeleted());
  }

  @Test
  void restoreShouldMakeArchivedProductActiveAgain() {
    this.productService.createProduct(new ProductBuilder().withName("Restore Product").build());
    Product saved = this.productService.listAll().getFirst();
    this.productService.archive(saved.getId());
    this.productService.restore(saved.getId());
    Product restored = this.productService.getProduct(saved.getId());
    assertFalse(restored.isDeleted());
  }

  @Test
  void removeShouldPermanentlyDeleteProduct() {
    this.productService.createProduct(new ProductBuilder().withName("Delete Product").build());
    Product saved = this.productService.listAll().getFirst();
    this.productService.archive(saved.getId());
    this.productService.remove(saved.getId());
    assertTrue(this.productService.listAll().isEmpty());
    assertThrows(
        EntityNotFoundException.class, () -> this.productService.getProduct(saved.getId()));
  }

  @Test
  void getProductShouldReturnExistingProduct() {
    this.productService.createProduct(new ProductBuilder().withName("Test Product").build());
    Product saved = this.productService.listAll().getFirst();
    Product result = this.productService.getProduct(saved.getId());
    assertEquals(saved.getId(), result.getId());
    assertEquals("Test Product", result.getName());
  }

  @Test
  void getProductShouldThrowWhenProductDoesNotExist() {
    EntityNotFoundException exception =
        assertThrows(EntityNotFoundException.class, () -> this.productService.getProduct(9999));
    assertEquals("The product could not be found.", exception.getMessage());
  }

  @Test
  void listAllShouldIncludeArchivedProducts() {
    this.productService.createProduct(
        new ProductBuilder()
            .withSku("ACT-001")
            .withBarcode("ACT-BAR")
            .withName("Active Product")
            .build());
    this.productService.createProduct(
        new ProductBuilder()
            .withSku("ARC-001")
            .withBarcode("ARC-BAR")
            .withName("Archived Product")
            .build());
    Product archived =
        this.productService.listAll().stream()
            .filter(product -> product.getName().equals("Archived Product"))
            .findFirst()
            .orElseThrow();
    this.productService.archive(archived.getId());
    List<Product> products = this.productService.listAll();
    assertEquals(2, products.size());
    assertTrue(products.stream().anyMatch(Product::isDeleted));
  }

  @Test
  void listDistinctUnitTypesShouldReturnUniqueSortedValues() {
    this.productService.createProduct(
        new ProductBuilder()
            .withSku("PRD-001")
            .withBarcode("BAR-001")
            .withUnitType("piece")
            .build());
    this.productService.createProduct(
        new ProductBuilder().withSku("PRD-002").withBarcode("BAR-002").withUnitType("box").build());
    this.productService.createProduct(
        new ProductBuilder()
            .withSku("PRD-003")
            .withBarcode("BAR-003")
            .withUnitType("each")
            .build());
    List<String> unitTypes = this.productService.listDistinctUnitTypes();
    assertEquals(List.of("box", "each", "piece"), unitTypes);
  }

  private void resetSchema() throws SQLException {
    try (Statement stmt = this.connection.createStatement()) {
      stmt.execute("DROP TABLE IF EXISTS products");
      String sql =
          """
          CREATE Table products (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              sku TEXT NOT NULL UNIQUE,
              barcode TEXT UNIQUE,
              name TEXT NOT NULL,
              category TEXT NOT NULL,
              unit_type TEXT NOT NULL DEFAULT 'each',
              tax_category TEXT NOT NULL DEFAULT 'standard',
              description TEXT,
              weight INTEGER NOT NULL DEFAULT 0,
              current_stock INTEGER NOT NULL DEFAULT 0,
              min_stock_level INTEGER NOT NULL DEFAULT 0,
              max_stock_level INTEGER NOT NULL DEFAULT 0,
              reordering_level INTEGER NOT NULL DEFAULT 5,
              selling_price_cents INTEGER NOT NULL DEFAULT 0,
              average_unit_cost_cents INTEGER NOT NULL DEFAULT 0,
              is_active INTEGER NOT NULL DEFAULT 1,
              created_at TEXT NOT NULL,
              updated_at TEXT NOT NULL,
              is_deleted INTEGER NOT NULL DEFAULT 0)
          """;
      stmt.execute(sql);
    }
  }
}
