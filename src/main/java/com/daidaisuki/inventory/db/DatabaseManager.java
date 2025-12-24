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
    try (Statement stmt = getConnection().createStatement()) {
      String createProductTable =
          "CREATE TABLE IF NOT EXISTS products ("
              + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
              + "sku TEXT UNIQUE,"
              + "name TEXT NOT NULL,"
              + "category TEXT NOT NULL,"
              + "current_stock INTEGER DEFAULT 0 NOT NULL,"
              + "selling_price_cents INTEGER NOT NULL,"
              + "reorder_level INTEGER DEFAULT 5,"
              + "is_active INTEGER DEFAULT 1"
              + ");";
      stmt.execute(createProductTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);");
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);");

      String createStockBatchesTable =
          "CREATE TABLE IF NOT EXISTS stock_batches ("
              + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
              + "product_id INTEGER NOT NULL,"
              + "quantity_received INTEGER NOT NULL,"
              + "quantity_remaining INTEGER NOT NULL,"
              + "unit_cost_cents INTEGER NOT NULL,"
              + "landed_cost_cents INTEGER NOT NULL,"
              + "received_date DATETIME DEFAULT CURRENT_TIMESTAMP,"
              + "FOREIGN KEY(product_id) REFERENCES products(id)"
              + ");";
      stmt.execute(createStockBatchesTable);
      stmt.execute(
          "CREATE INDEX IF NOT EXISTS idx_batches_product_id ON stock_batches(product_id);");

      String createCustomerTable =
          "CREATE TABLE IF NOT EXISTS customers ("
              + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
              + "full_name TEXT NOT NULL,"
              + "phone TEXT,"
              + "email TEXT,"
              + "address TEXT,"
              + "acquisition_source TEXT"
              + ");";
      stmt.execute(createCustomerTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_customers_name ON customers(full_name);");

      String createOrderTable =
          "CREATE TABLE IF NOT EXISTS orders ("
              + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
              + "customer_id INTEGER NOT NULL,"
              + "order_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
              + "fulfillment_type TEXT NOT NULL,"
              + "fulfillment_status TEXT NOT NULL,"
              + "total_items INTEGER NOT NULL,"
              + "subtotal_cents INTEGER NOT NULL,"
              + "discount_amount_cents INTEGER DEFAULT 0,"
              + "shipping_cost_cents INTEGER DEFAULT 0,"
              + "shipping_cost_actual_cents INTEGER DEFAULT 0,"
              + "final_amount_cents INTEGER NOT NULL,"
              + "payment_method TEXT,"
              + "tracking_number TEXT,"
              + "FOREIGN KEY(customer_id) REFERENCES customers(id)"
              + ");";
      stmt.execute(createOrderTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);");
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_orders_order_date ON orders(order_timestamp);");
      stmt.execute(
          "CREATE INDEX IF NOT EXISTS idx_orders_fulfillment ON orders(fulfillment_type,"
              + " fulfillment_status);");

      String createOrderItemTable =
          "CREATE TABLE IF NOT EXISTS order_items ("
              + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
              + "order_id INTEGER NOT NULL,"
              + "product_id INTEGER NOT NULL,"
              + "batch_id INTEGER NOT NULL,"
              + "quantity INTEGER NOT NULL,"
              + "unit_price_cents INTEGER NOT NULL,"
              + "unit_cost_at_sale_cents INTEGER NOT NULL,"
              + "FOREIGN KEY(order_id) REFERENCES orders(id),"
              + "FOREIGN KEY(product_id) REFERENCES products(id),"
              + "FOREIGN KEY(batch_id) REFERENCES stock_batches(id)"
              + ");";
      stmt.execute(createOrderItemTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);");
      stmt.execute(
          "CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);");
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_order_items_batch_id ON order_items(batch_id);");
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
