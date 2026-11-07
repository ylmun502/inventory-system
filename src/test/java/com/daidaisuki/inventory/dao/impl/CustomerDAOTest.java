package com.daidaisuki.inventory.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.model.builder.CustomerBuilder;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerDAOTest {
  private Connection connection;
  private CustomerDAO customerDAO;

  @BeforeEach
  void setUp() throws SQLException {
    this.connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    this.resetSchema();
    this.customerDAO = new CustomerDAO(this.connection);
  }

  @AfterEach
  void tearDown() throws SQLException {
    if (this.connection != null && !this.connection.isClosed()) {
      this.connection.close();
    }
  }

  @Test
  void saveShouldPersistCustomer() {
    Customer customer =
        new CustomerBuilder()
            .withFullName("Jane Doe")
            .withPhoneNumber("2101689988")
            .withEmail("jane@example.com")
            .withAddress("123 Main St")
            .withAcquisitionSource("Instagram")
            .build();
    Customer saved = this.customerDAO.save(customer);
    assertTrue(saved.getId() > 0);
    assertEquals("Jane Doe", saved.getFullName());
    assertEquals("2101689988", saved.getPhoneNumber());
    assertEquals("jane@example.com", saved.getEmail());
    assertEquals("123 Main St", saved.getAddress());
    assertEquals("Instagram", saved.getAcquisitionSource());
    assertEquals(0, saved.getTotalOrders());
    assertEquals(0, saved.getTotalSpent().compareTo(BigDecimal.ZERO));
    assertEquals(0, saved.getTotalDiscount().compareTo(BigDecimal.ZERO));
    assertEquals(0, saved.getAverageOrderValue().compareTo(BigDecimal.ZERO));
    assertNull(saved.getLastOrderDate());
    assertFalse(saved.isDeleted());
    Customer persisted = this.customerDAO.findById(saved.getId()).orElseThrow();
    assertEquals(saved.getId(), persisted.getId());
    assertEquals(saved.getFullName(), persisted.getFullName());
    assertEquals(saved.getPhoneNumber(), persisted.getPhoneNumber());
    assertEquals(saved.getEmail(), persisted.getEmail());
    assertEquals(saved.getAddress(), persisted.getAddress());
    assertEquals(saved.getAcquisitionSource(), persisted.getAcquisitionSource());
    assertEquals(saved.getTotalOrders(), persisted.getTotalOrders());
    assertEquals(0, persisted.getTotalSpent().compareTo(saved.getTotalSpent()));
    assertEquals(0, persisted.getTotalDiscount().compareTo(saved.getTotalDiscount()));
    assertEquals(0, persisted.getAverageOrderValue().compareTo(saved.getAverageOrderValue()));
    assertEquals(saved.getLastOrderDate(), persisted.getLastOrderDate());
    assertEquals(saved.getCreatedAt(), persisted.getCreatedAt());
    assertEquals(saved.getUpdatedAt(), persisted.getUpdatedAt());
    assertEquals(saved.isDeleted(), persisted.isDeleted());
  }

  @Test
  void findByIdShouldReturnExistingCustomer() {
    Customer saved =
        this.customerDAO.save(new CustomerBuilder().withFullName("Existing Customer").build());
    Customer result = this.customerDAO.findById(saved.getId()).orElseThrow();
    assertEquals(saved.getId(), result.getId());
    assertEquals("Existing Customer", result.getFullName());
  }

  @Test
  void findByIdShouldReturnEmptyWhenCustomerDoesNotExist() {
    assertTrue(this.customerDAO.findById(9999).isEmpty());
  }

  @Test
  void findAllShouldReturnAllCustomersOrderedByName() {
    this.customerDAO.save(new CustomerBuilder().withFullName("Zulu Customer").build());
    this.customerDAO.save(new CustomerBuilder().withFullName("Alpha Customer").build());
    List<Customer> customers = this.customerDAO.findAll();
    assertEquals(2, customers.size());
    assertEquals("Alpha Customer", customers.get(0).getFullName());
    assertEquals("Zulu Customer", customers.get(1).getFullName());
  }

  @Test
  void updateShouldPersistEditableFields() {
    Customer saved =
        this.customerDAO.save(
            new CustomerBuilder()
                .withFullName("Original Customer")
                .withPhoneNumber("1111111111")
                .withEmail("old@example.com")
                .withAddress("Old Address")
                .withAcquisitionSource("Instagram")
                .build());
    Customer updated =
        new CustomerBuilder()
            .withId(saved.getId())
            .withFullName("Updated Customer")
            .withPhoneNumber("2222222222")
            .withEmail("new@example.com")
            .withAddress("New Address")
            .withAcquisitionSource("Facebook")
            .build();
    this.customerDAO.update(updated);
    Customer result = this.customerDAO.findById(saved.getId()).orElseThrow();
    assertEquals("Updated Customer", result.getFullName());
    assertEquals("2222222222", result.getPhoneNumber());
    assertEquals("new@example.com", result.getEmail());
    assertEquals("New Address", result.getAddress());
    assertEquals("Facebook", result.getAcquisitionSource());
  }

  @Test
  void archiveShouldMarkCustomerDeleted() {
    Customer saved =
        this.customerDAO.save(new CustomerBuilder().withFullName("Archive Customer").build());
    this.customerDAO.archive(saved.getId());
    Customer archived = this.customerDAO.findById(saved.getId()).orElseThrow();
    assertTrue(archived.isDeleted());
  }

  @Test
  void findByIdShouldIncludeArchivedCustomer() {
    Customer saved =
        this.customerDAO.save(new CustomerBuilder().withFullName("Archive Customer").build());
    this.customerDAO.archive(saved.getId());
    Optional<Customer> result = this.customerDAO.findById(saved.getId());
    assertTrue(result.isPresent());
    assertTrue(result.orElseThrow().isDeleted());
  }

  @Test
  void restoreShouldMakeArchivedCustomerActiveAgain() {
    Customer saved =
        this.customerDAO.save(new CustomerBuilder().withFullName("Restore Customer").build());
    this.customerDAO.archive(saved.getId());
    this.customerDAO.restore(saved.getId());
    Customer restored = this.customerDAO.findById(saved.getId()).orElseThrow();
    assertFalse(restored.isDeleted());
  }

  @Test
  void removeShouldPermanentlyDeleteCustomer() {
    Customer saved =
        this.customerDAO.save(new CustomerBuilder().withFullName("Delete Customer").build());
    this.customerDAO.remove(saved.getId());
    assertTrue(this.customerDAO.findById(saved.getId()).isEmpty());
  }

  @Test
  void findByIdShouldMapCustomerSummaryFields() throws SQLException {
    Customer saved =
        this.customerDAO.save(new CustomerBuilder().withFullName("Summary Customer").build());
    this.replaceSummaryViewWithTestValues();
    Customer result = this.customerDAO.findById(saved.getId()).orElseThrow();
    assertEquals(4, result.getTotalOrders());
    assertEquals(0, result.getTotalSpent().compareTo(new BigDecimal("200.00")));
    assertEquals(0, result.getTotalDiscount().compareTo(new BigDecimal("20.00")));
    assertEquals(0, result.getAverageOrderValue().compareTo(new BigDecimal("50.00")));
    assertEquals(OffsetDateTime.parse("2026-06-15T10:30:00Z"), result.getLastOrderDate());
  }

  private void resetSchema() throws SQLException {
    try (Statement stmt = this.connection.createStatement()) {
      stmt.execute("DROP VIEW IF EXISTS customer_summary");
      stmt.execute("DROP TABLE IF EXISTS customers");
      String tableSql =
          """
          CREATE Table customers (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              full_name TEXT NOT NULL,
              phone_number TEXT,
              email TEXT,
              address TEXT,
              acquisition_source TEXT NOT NULL,
              created_at TEXT NOT NULL,
              updated_at TEXT NOT NULL,
              is_deleted INTEGER NOT NULL DEFAULT 0)
          """;
      String viewSql =
          """
              CREATE VIEW customer_summary AS
              SELECT
                id,
                full_name,
                phone_number,
                email,
                address,
                acquisition_source,
                0 AS total_orders,
                0 AS total_spent_cents,
                0 AS total_discount_cents,
                NULL AS last_order_date,
                created_at,
                updated_at,
                is_deleted
              FROM customers
          """;
      stmt.execute(tableSql);
      stmt.execute(viewSql);
    }
  }

  private void replaceSummaryViewWithTestValues() throws SQLException {
    try (Statement stmt = this.connection.createStatement()) {
      stmt.execute("DROP VIEW IF EXISTS customer_summary");
      String sql =
          """
              CREATE VIEW customer_summary AS
              SELECT
                id,
                full_name,
                phone_number,
                email,
                address,
                acquisition_source,
                4 AS total_orders,
                20000 AS total_spent_cents,
                2000 AS total_discount_cents,
                "2026-06-15T10:30:00Z" AS last_order_date,
                created_at,
                updated_at,
                is_deleted
              FROM customers
          """;
      stmt.execute(sql);
    }
  }
}
