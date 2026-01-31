package com.daidaisuki.inventory.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
  // database file name
  private static final String DB_URL = "jdbc:sqlite:inventory.db";

  // Singleton connection
  private static Connection connection = null;

  // Connect to the SQLite database
  public static synchronized Connection getConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
      connection = DriverManager.getConnection(DB_URL);
      // Set PRAGMA for every new connection
      try (Statement stmt = connection.createStatement()) {
        stmt.execute("PRAGMA foreign_keys = ON;");
        // Optimization for improving concurrency significantly with WAL mode
        stmt.execute("PRAGMA journal_mode = WAL;");
      }
    }
    return connection;
  }

  // Initialize DB schema (tables etc.)
  public static void initializeDatabase() throws SQLException {
    Connection conn = getConnection();
    try (Statement stmt = conn.createStatement()) {
      String createProductTable =
          """
          CREATE TABLE IF NOT EXISTS products (
                  -- Primary Identity
                  id                    INTEGER PRIMARY KEY AUTOINCREMENT,

                  -- Core Data
                  sku                   TEXT UNIQUE,
                  barcode               TEXT UNIQUE,
                  name                  TEXT NOT NULL,
                  category              TEXT NOT NULL,
                  unit_type             TEXT DEFAULT 'each',
                  tax_category          TEXT DEFAULT 'standard',
                  description           TEXT,
                  weight                INTEGER NOT NULL,
                  current_stock         INTEGER DEFAULT 0 NOT NULL CHECK (current_stock >= 0),
                  min_stock_level       INTEGER DEFAULT 2,
                  max_stock_level       INTEGER DEFAULT 100,
                  reordering_level      INTEGER DEFAULT 5,
                  selling_price_cents   INTEGER NOT NULL,
                  is_active             INTEGER DEFAULT 1,

                  -- Audit Metadata
                  created_at          DATETIME NOT NULL,
                  updated_at          DATETIME NOT NULL,
                  is_deleted          INTEGER NOT NULL
                  );
          """;
      stmt.execute(createProductTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);");
      stmt.execute(
          "CREATE INDEX IF NOT EXISTS idx_products_sku_barcode ON products(sku, barcode);");

      String createSupplierTable =
          """
          CREATE TABLE IF NOT EXIST suppliers (
                  -- Primary Identity
                  id                    INTEGER PRIMARY KEY AUTOINCREMENT,

                  -- Core Data
                  name                  TEXT,
                  short_code            TEXT,

                  -- Audit Metadata
                  created_at            DATETIME NOT NULL,
                  updated_at            DATETIME NOT NULL,
                  is_deleted            INTEGER NOT NULL
                  );
          """;
      stmt.execute(createSupplierTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_suppliers_name ON suppliers(name);");

      String createStockBatchesTable =
          """
          CREATE TABLE IF NOT EXISTS stock_batches (
                  -- Primary Identity
                  id                    INTEGER PRIMARY KEY AUTOINCREMENT,

                  -- Foreign Keys / Relationships
                  product_id            INTEGER NOT NULL,
                  supplier_id           INTEGER NOT NULL,

                  -- Core Data
                  batch_code            TEXT,
                  expiry_date           DATETIME,
                  quantity_received     INTEGER NOT NULL,
                  quantity_remaining    INTEGER NOT NULL,
                  unit_cost_cents       INTEGER NOT NULL,
                  landed_cost_cents     INTEGER NOT NULL,

                  -- Audit Metadata
                  created_at            DATETIME NOT NULL,
                  updated_at            DATETIME NOT NULL,
                  is_deleted            INTEGER NOT NULL,

                  -- Constraints
                  FOREIGN KEY(product_id) REFERENCES products(id)
                  );
          """;
      stmt.execute(createStockBatchesTable);
      stmt.execute(
          "CREATE INDEX IF NOT EXISTS idx_batches_product_id ON stock_batches(product_id);");

      String createInventoryTransactionsTable =
          """
          CREATE TABLE IF NOT EXISTS inventory_transactions (
                  -- Primary Identity
                  id                  INTEGER PRIMARY KEY AUTOINCREMENT,

                  -- Foreign Keys / Relationships
                  product_id          INTEGER NOT NULL,
                  batch_id            INTEGER NOT NULL,
                  user_id             INTEGER NOT NULL,
                  reference_id        INTEGER NOT NULL,

                  -- Transaction Data
                  change_amount       INTEGER NOT NULL,
                  transaction_type    TEXT NOT NULL,
                  reason_code         TEXT NOT NULL,

                  -- Audit Metadata
                  created_at          DATETIME NOT NULL,
                  updated_at          DATETIME NOT NULL,
                  is_deleted          INTEGER NOT NULL,

                  -- Constraints
                  FOREIGN KEY(product_id) REFERENCES products(id),
                  FOREIGN KEY(batch_id) REFERENCES stock_batches(id)
                  );
          """;
      stmt.execute(createInventoryTransactionsTable);
      stmt.execute(
          """
          CREATE INDEX IF NOT EXISTS idx_transactions_product_id ON inventory_transactions(product_id);
          """);
      stmt.execute(
          """
          CREATE INDEX IF NOT EXISTS idx_transactions_batch_id ON inventory_transactions(batch_id);
          """);
      stmt.execute(
          """
          CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON inventory_transactions(created_at);
          """);
      stmt.execute(
          """
          CREATE INDEX IF NOT EXISTS idx_transactions_type_date ON inventory_transactions(transaction_type, created_at);
          """);

      String createCustomerTable =
          """
          CREATE TABLE IF NOT EXISTS customers (
                  -- Primary Identity
                  id                    INTEGER PRIMARY KEY AUTOINCREMENT,

                  -- Core Data
                  full_name             TEXT NOT NULL,
                  phone_number          TEXT,
                  email                 TEXT,
                  address               TEXT,
                  acquisition_source    TEXT NOT NULL,

                  -- Audit Metadata
                  created_at            DATETIME NOT NULL,
                  updated_at            DATETIME NOT NULL,
                  is_deleted            INTEGER NOT NULL
                  );
          """;
      stmt.execute(createCustomerTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_customers_name ON customers(full_name);");

      String createCustomerSummaryView =
          """
          CREATE VIEW IF NOT EXISTS customer_summary AS
          SELECT
              c.id,
              c.full_name,
              c.phone_number,
              c.email,
              c.address,
              c.acquisition_source,
              c.created_at,
              c.updated_at,
              c.is_deleted,
              COUNT(o.id) AS total_orders,
              COALESCE(SUM(o.final_amount_cents), 0) AS total_spent_cents,
              COALESCE(SUM(o.discount_amount_cents), 0) AS total_discount_cents,
              MAX(o.created_at) AS last_order_date
          FROM customers c
          LEFT JOIN orders o ON c.id = o.customer_id AND o.is_deleted = 0
          GROUP BY c.id;
          """;
      stmt.execute(createCustomerSummaryView);

      String createOrderTable =
          """
          CREATE TABLE IF NOT EXISTS orders (
                  -- Primary Identity
                  id                      INTEGER PRIMARY KEY AUTOINCREMENT,

                  -- Foreign Keys / Relationships
                  customer_id                   INTEGER NOT NULL,

                  -- Core Data
                  fulfillment_type              TEXT NOT NULL,
                  fulfillment_status            TEXT NOT NULL,
                  total_items                   INTEGER NOT NULL,
                  subtotal_cents                INTEGER NOT NULL,
                  tax_amount_cents              INTEGER DEFAULT 0,
                  discount_amount_cents         INTEGER DEFAULT 0,
                  shipping_cost_cents           INTEGER DEFAULT 0,
                  shipping_cost_actual_cents    INTEGER DEFAULT 0,
                  final_amount_cents            INTEGER NOT NULL,
                  payment_method                TEXT,
                  tracking_number               TEXT,

                  -- Audit Metadata
                  created_at                    DATETIME NOT NULL,
                  updated_at                    DATETIME NOT NULL,
                  is_deleted                    INTEGER NOT NULL,

                  -- Constraints
                  FOREIGN KEY(customer_id)      REFERENCES customers(id)
                  );
          """;
      stmt.execute(createOrderTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);");
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_orders_order_date ON orders(created_at);");
      stmt.execute(
          """
          CREATE INDEX IF NOT EXISTS idx_orders_fulfillment ON orders(fulfillment_type, fulfillment_status);
          """);

      String createOrderItemTable =
          """
          CREATE TABLE IF NOT EXISTS order_items (
                  -- Primary Identity
                  id                          INTEGER PRIMARY KEY AUTOINCREMENT,

                  -- Foreign Keys / Relationships
                  order_id                    INTEGER NOT NULL,
                  product_id                  INTEGER NOT NULL,
                  batch_id                    INTEGER NOT NULL,

                  -- Core Data
                  quantity                    INTEGER NOT NULL,
                  unit_price_at_sale_cents    INTEGER NOT NULL,
                  unit_cost_at_sale_cents     INTEGER NOT NULL,

                  -- Audit Metadata
                  created_at                  DATETIME DEFAULT NOT NULL,
                  updated_at                  DATETIME DEFAULT NOT NULL,
                  is_deleted                  INTEGER NOT NULL,

                  -- Constraints
                  FOREIGN KEY(order_id) REFERENCES orders(id),
                  FOREIGN KEY(product_id) REFERENCES products(id),
                  FOREIGN KEY(batch_id) REFERENCES stock_batches(id)
                  );
          """;
      stmt.execute(createOrderItemTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);");
      stmt.execute(
          "CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);");
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_order_items_batch_id ON order_items(batch_id);");

      stmt.execute(
          """
          INSERT OR IGNORE INTO products (id, sku, barcode, name, category, weight, selling_price_cents)
          VALUES (0, 'SYSTEM-ADJ', '00000000', 'System Adjustment', 'System', 0, 0);
          """);

      String insertDummyBatch =
          """
          INSERT OR IGNORE INTO stock_batches (id, product_id, quantity_received,
              quantity_remaining, unit_cost_cents, landed_cost_cents)
          VALUES (0, 0, 0, 0, 0, 0);
          """;
      stmt.execute(insertDummyBatch);
    }
  }

  public static void closeConnection() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
