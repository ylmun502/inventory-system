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
  public static Connection getConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
      connection = DriverManager.getConnection(DB_URL);
      try (Statement stmt = connection.createStatement()) {
        stmt.execute("PRAGMA foreign_keys = ON;");
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
              + "name TEXT NOT NULL,"
              + "category TEXT NOT NULL,"
              + "stock INTEGER NOT NULL,"
              + "selling_price REAL NOT NULL,"
              + "purchase_cost REAL NOT NULL,"
              + "shipping_cost REAL NOT NULL"
              + ");";
      stmt.execute(createProductTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);");
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);");

      String createCustomerTable =
          "CREATE TABLE IF NOT EXISTS customers ("
              + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
              + "name TEXT NOT NULL,"
              + "phone_number TEXT,"
              + "email TEXT,"
              + "address TEXT,"
              + "platform TEXT NOT NULL"
              + ");";
      stmt.execute(createCustomerTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_customers_name ON customers(name);");

      String createOrderTable =
          "CREATE TABLE IF NOT EXISTS orders ("
              + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
              + "customer_id INTEGER NOT NULL,"
              + "order_date TEXT NOT NULL,"
              + "total_items INTEGER NOT NULL,"
              + "total_amount REAL NOT NULL,"
              + "discount_amount REAL NOT NULL,"
              + "payment_method TEXT,"
              + "FOREIGN KEY(customer_id) REFERENCES customers(id)"
              + ");";
      stmt.execute(createOrderTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);");
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_orders_order_date ON orders(order_date);");

      String createOrderItemTable =
          "CREATE TABLE IF NOT EXISTS order_items ("
              + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
              + "order_id INTEGER NOT NULL,"
              + "product_id INTEGER NOT NULL,"
              + "quantity INTEGER NOT NULL,"
              + "unit_price REAL NOT NULL,"
              + "cost_at_sale REAL NOT NULL,"
              + "FOREIGN KEY(order_id) REFERENCES orders(id),"
              + "FOREIGN KEY(product_id) REFERENCES products(id)"
              + ");";
      // last_modified DATETIME DEFAULT CURRENT_TIMESTAMP,
      // sync_status TEXT DEFAULT 'PENDING'
      stmt.execute(createOrderItemTable);
      stmt.execute("CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);");
      stmt.execute(
          "CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);");
    }
  }
}
