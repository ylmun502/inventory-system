package com.daidaisuki.inventory.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daidaisuki.inventory.dao.impl.InventoryTransactionDAO;
import com.daidaisuki.inventory.dao.impl.ProductDAO;
import com.daidaisuki.inventory.dao.impl.StockBatchDAO;
import com.daidaisuki.inventory.enums.TransactionType;
import com.daidaisuki.inventory.exception.EntityNotFoundException;
import com.daidaisuki.inventory.model.InventoryTransaction;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.model.StockBatch;
import com.daidaisuki.inventory.model.builder.ProductBuilder;
import com.daidaisuki.inventory.model.dto.StockReceiveRequest;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryServiceTest {

  private Connection connection;
  private InventoryService inventoryService;
  private ProductDAO productDAO;
  private StockBatchDAO stockBatchDAO;
  private InventoryTransactionDAO inventoryTransactionDAO;

  @BeforeEach
  void setUp() throws SQLException {
    this.connection = DriverManager.getConnection("jdbc:sqlite::memory:");

    try (Statement statement = this.connection.createStatement()) {
      statement.execute("PRAGMA foreign_keys = ON");
    }

    this.resetSchema();

    this.inventoryService = new InventoryService(this.connection);

    this.productDAO = new ProductDAO(this.connection);

    this.stockBatchDAO = new StockBatchDAO(this.connection);

    this.inventoryTransactionDAO = new InventoryTransactionDAO(this.connection);
  }

  @AfterEach
  void tearDown() throws SQLException {
    if (this.connection != null && !this.connection.isClosed()) {
      this.connection.close();
    }
  }

  @Test
  void receiveNewStockShouldIncreaseProductStock() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());

    StockReceiveRequest request =
        this.createReceiveRequest(saved.getId(), 25, new BigDecimal("8.50"));

    this.inventoryService.receiveNewStock(request, 1);

    Product result = this.productDAO.findById(saved.getId()).orElseThrow();

    assertEquals(25, result.getCurrentStock());
  }

  @Test
  void receiveNewStockShouldUpdateWeightedAverageUnitCost() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());

    // Existing inventory:
    // 10 units × $5.00 = $50.00
    assertTrue(this.productDAO.updateStockTotalAndCost(saved.getId(), 10, new BigDecimal("5.00")));

    // Incoming inventory:
    // 5 units × $8.00 = $40.00
    //
    // New total value = $90.00
    // New quantity = 15
    // New average cost = $6.00
    StockReceiveRequest request =
        this.createReceiveRequest(saved.getId(), 5, new BigDecimal("8.00"));

    this.inventoryService.receiveNewStock(request, 1);

    Product result = this.productDAO.findById(saved.getId()).orElseThrow();

    assertEquals(15, result.getCurrentStock());

    assertEquals(0, result.getAverageUnitCost().compareTo(new BigDecimal("6.00")));
  }

  @Test
  void receiveNewStockShouldCreateStockBatch() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());

    StockReceiveRequest request =
        new StockReceiveRequest(
            saved.getId(),
            7,
            "BCH-001",
            20,
            new BigDecimal("12.75"),
            OffsetDateTime.parse("2027-12-31T00:00:00Z"),
            "Initial stock");

    this.inventoryService.receiveNewStock(request, 42);

    List<StockBatch> batches = this.stockBatchDAO.findAllByProductId(saved.getId());

    assertEquals(1, batches.size());

    StockBatch batch = batches.getFirst();

    assertEquals(saved.getId(), batch.getProductId());

    assertEquals(7, batch.getSupplierId());

    assertEquals("BCH-001", batch.getBatchCode());

    assertEquals(20, batch.getQuantityReceived());

    assertEquals(20, batch.getQuantityRemaining());

    assertEquals(0, batch.getUnitCost().compareTo(new BigDecimal("12.75")));

    assertEquals(OffsetDateTime.parse("2027-12-31T00:00:00Z"), batch.getExpiryDate());

    assertNotNull(batch.getCreatedAt());

    assertNotNull(batch.getUpdatedAt());

    assertFalse(batch.isDeleted());
  }

  @Test
  void receiveNewStockShouldCreateInventoryTransaction() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());

    StockReceiveRequest request =
        new StockReceiveRequest(
            saved.getId(), 7, "BCH-001", 20, new BigDecimal("12.75"), null, "Initial stock");

    this.inventoryService.receiveNewStock(request, 42);

    List<StockBatch> batches = this.stockBatchDAO.findAllByProductId(saved.getId());

    StockBatch batch = batches.getFirst();

    List<InventoryTransaction> transactions =
        this.inventoryTransactionDAO.findAllByProductId(saved.getId());

    assertEquals(1, transactions.size());

    InventoryTransaction transaction = transactions.getFirst();

    assertEquals(saved.getId(), transaction.getProductId());

    assertEquals(batch.getId(), transaction.getBatchId());

    assertEquals(42, transaction.getUserId());

    assertEquals(0, transaction.getReferenceId());

    assertEquals(20, transaction.getChangeAmount());

    assertEquals(TransactionType.STOCK_IN, transaction.getTransactionType());

    assertEquals("Initial stock", transaction.getReasonCode());

    assertNotNull(transaction.getCreatedAt());

    assertNotNull(transaction.getUpdatedAt());

    assertFalse(transaction.isDeleted());
  }

  @Test
  void receiveNewStockShouldThrowWhenProductDoesNotExist() {
    StockReceiveRequest request = this.createReceiveRequest(9999, 10, new BigDecimal("5.00"));

    assertThrows(
        EntityNotFoundException.class, () -> this.inventoryService.receiveNewStock(request, 1));
  }

  @Test
  void receiveNewStockShouldRejectZeroQuantity() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());

    StockReceiveRequest request =
        this.createReceiveRequest(saved.getId(), 0, new BigDecimal("5.00"));

    assertThrows(
        IllegalArgumentException.class, () -> this.inventoryService.receiveNewStock(request, 1));
  }

  @Test
  void receiveNewStockShouldRejectNegativeQUantity() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());
    StockReceiveRequest request =
        this.createReceiveRequest(saved.getId(), -2, new BigDecimal("8.00"));
    assertThrows(
        IllegalArgumentException.class, () -> this.inventoryService.receiveNewStock(request, 1));
  }

  @Test
  void receiveNewStockShouldRejectNullUnitCost() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());
    StockReceiveRequest request = this.createReceiveRequest(saved.getId(), 10, null);
    assertThrows(
        IllegalArgumentException.class, () -> this.inventoryService.receiveNewStock(request, 1));
  }

  @Test
  void receiveNewStockShouldRejectNegativeUnitCost() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());
    StockReceiveRequest request =
        this.createReceiveRequest(saved.getId(), 10, new BigDecimal("-8.00"));
    assertThrows(
        IllegalArgumentException.class, () -> this.inventoryService.receiveNewStock(request, 1));
  }

  @Test
  void receiveNewStockShouldAllowZeroUnitCost() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());
    StockReceiveRequest request = this.createReceiveRequest(saved.getId(), 10, BigDecimal.ZERO);
    assertDoesNotThrow(() -> this.inventoryService.receiveNewStock(request, 1));
    Product result = this.productDAO.findById(saved.getId()).orElseThrow();
    assertEquals(10, result.getCurrentStock());
    assertEquals(0, result.getAverageUnitCost().compareTo(BigDecimal.ZERO));
  }

  @Test
  void receiveNewStockShouldRejectNullReason() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());
    StockReceiveRequest request =
        new StockReceiveRequest(
            saved.getId(), 1, "BCH-001", 10, new BigDecimal("8.00"), null, null);
    assertThrows(
        IllegalArgumentException.class, () -> this.inventoryService.receiveNewStock(request, 1));
  }

  @Test
  void receiveNewStockShouldRejectBlankReason() {
    Product saved =
        this.productDAO.save(
            new ProductBuilder().withSku("PRD-001").withBarcode("BAR-001").build());
    StockReceiveRequest request =
        new StockReceiveRequest(
            saved.getId(), 1, "BCH-001", 10, new BigDecimal("8.00"), null, "   ");
    assertThrows(
        IllegalArgumentException.class, () -> this.inventoryService.receiveNewStock(request, 1));
  }

  private StockReceiveRequest createReceiveRequest(
      int productId, int quantity, BigDecimal unitCost) {

    return new StockReceiveRequest(
        productId, 1, "BCH-001", quantity, unitCost, null, "Stock received");
  }

  private void resetSchema() throws SQLException {

    try (Statement statement = this.connection.createStatement()) {

      statement.execute("DROP TABLE IF EXISTS inventory_transactions");

      statement.execute("DROP TABLE IF EXISTS stock_batches");

      statement.execute("DROP TABLE IF EXISTS products");

      statement.execute(
          """
          CREATE TABLE products (
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
            is_deleted INTEGER NOT NULL DEFAULT 0
          )
          """);

      statement.execute(
          """
          CREATE TABLE stock_batches (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            product_id INTEGER NOT NULL,
            supplier_id INTEGER NOT NULL,
            batch_code TEXT,
            expiry_date DATETIME,
            quantity_received INTEGER NOT NULL,
            quantity_remaining INTEGER NOT NULL,
            unit_cost_cents INTEGER NOT NULL,
            landed_cost_cents INTEGER NOT NULL,
            created_at DATETIME NOT NULL,
            updated_at DATETIME NOT NULL,
            is_deleted INTEGER NOT NULL,
            FOREIGN KEY(product_id)
              REFERENCES products(id)
          )
          """);

      statement.execute(
          """
          CREATE TABLE inventory_transactions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            product_id INTEGER NOT NULL,
            batch_id INTEGER NOT NULL,
            user_id INTEGER NOT NULL,
            reference_id INTEGER NOT NULL,
            change_amount INTEGER NOT NULL,
            transaction_type TEXT NOT NULL,
            reason_code TEXT NOT NULL,
            created_at DATETIME NOT NULL,
            updated_at DATETIME NOT NULL,
            is_deleted INTEGER NOT NULL,
            FOREIGN KEY(product_id)
              REFERENCES products(id),
            FOREIGN KEY(batch_id)
              REFERENCES stock_batches(id)
          )
          """);
    }
  }
}
