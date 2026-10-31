package com.daidaisuki.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daidaisuki.inventory.exception.EntityNotFoundException;
import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.model.builder.CustomerBuilder;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerServiceTest {
  private Connection connection;
  private CustomerService customerService;

  @BeforeEach
  void setUp() throws SQLException {
    this.connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    this.resetSchema();
    this.customerService = new CustomerService(this.connection);
  }

  @AfterEach
  void tearDown() throws SQLException {
    if (this.connection != null && !this.connection.isClosed()) {
      this.connection.close();
    }
  }

  @Test
  void createCustomerShouldPersistCustomer() {
    Customer customer =
        new CustomerBuilder()
            .withFullName("Jane Doe")
            .withPhoneNumber("2101689988")
            .withEmail("jane@example.com")
            .withAddress("123 Main St")
            .withAcquisitionSource("Instagram")
            .build();
    this.customerService.createCustomer(customer);
    List<Customer> customers = this.customerService.listAll();
    assertEquals(1, customers.size());
    Customer saved = customers.getFirst();
    assertEquals("Jane Doe", saved.getFullName());
    assertEquals("2101689988", saved.getPhoneNumber());
    assertEquals("jane@example.com", saved.getEmail());
    assertEquals("123 Main St", saved.getAddress());
    assertEquals("Instagram", saved.getAcquisitionSource());
    assertEquals(0, saved.getTotalOrders());
    assertEquals(0, saved.getTotalSpent().compareTo(BigDecimal.ZERO));
    assertEquals(0, saved.getTotalDiscount().compareTo(BigDecimal.ZERO));
    assertEquals(0, saved.getAverageOrderValue().compareTo(BigDecimal.ZERO));
    assertFalse(saved.isDeleted());
  }

  @Test
  void updateCustomerShouldPersistEditableFields() {
    this.customerService.createCustomer(
        new CustomerBuilder()
            .withFullName("Original Customer")
            .withPhoneNumber("1111111111")
            .withEmail("old@example.com")
            .withAddress("Old Address")
            .withAcquisitionSource("Instagram")
            .build());
    Customer saved = this.customerService.listAll().getFirst();
    Customer updated =
        new CustomerBuilder()
            .withId(saved.getId())
            .withFullName("Updated Customer")
            .withPhoneNumber("2222222222")
            .withEmail("new@example.com")
            .withAddress("New Address")
            .withAcquisitionSource("Facebook")
            .build();
    this.customerService.updateCustomer(updated);
    Customer result = this.customerService.getCustomer(saved.getId());
    assertEquals("Updated Customer", result.getFullName());
    assertEquals("2222222222", result.getPhoneNumber());
    assertEquals("new@example.com", result.getEmail());
    assertEquals("New Address", result.getAddress());
    assertEquals("Facebook", result.getAcquisitionSource());
  }

  @Test
  void archiveShouldMarkCustomerDeleted() {
    this.customerService.createCustomer(
        new CustomerBuilder().withFullName("Archive Customer").build());
    Customer saved = this.customerService.listAll().getFirst();
    this.customerService.archive(saved.getId());
    Customer archived = this.customerService.getCustomer(saved.getId());
    assertTrue(archived.isDeleted());
  }

  @Test
  void restoreShouldMakeArchivedCustomerActiveAgain() {
    this.customerService.createCustomer(
        new CustomerBuilder().withFullName("Restore Customer").build());
    Customer saved = this.customerService.listAll().getFirst();
    this.customerService.archive(saved.getId());
    this.customerService.restore(saved.getId());
    Customer restored = this.customerService.getCustomer(saved.getId());
    assertFalse(restored.isDeleted());
  }

  @Test
  void removeShouldPermanentlyDeleteCustomer() {
    this.customerService.createCustomer(
        new CustomerBuilder().withFullName("Delete Customer").build());
    Customer saved = this.customerService.listAll().getFirst();
    this.customerService.archive(saved.getId());
    this.customerService.remove(saved.getId());
    assertTrue(this.customerService.listAll().isEmpty());
    assertThrows(
        EntityNotFoundException.class, () -> this.customerService.getCustomer(saved.getId()));
  }

  @Test
  void getCustomerShouldReturnExistingCustomer() {
    this.customerService.createCustomer(new CustomerBuilder().withFullName("Jane Doe").build());
    Customer saved = this.customerService.listAll().getFirst();
    Customer result = this.customerService.getCustomer(saved.getId());
    assertEquals(saved.getId(), result.getId());
    assertEquals("Jane Doe", result.getFullName());
  }

  @Test
  void getCustomerShouldThrowWhenCustomerDoesNotExist() {
    EntityNotFoundException exception =
        assertThrows(EntityNotFoundException.class, () -> this.customerService.getCustomer(9999));
    assertEquals("The customer could not be found.", exception.getMessage());
  }

  @Test
  void listAllShouldIncludeArchivedCustomers() {
    this.customerService.createCustomer(
        new CustomerBuilder().withFullName("Active Customer").build());
    this.customerService.createCustomer(
        new CustomerBuilder().withFullName("Archived Customer").build());
    Customer archived =
        this.customerService.listAll().stream()
            .filter(customer -> customer.getFullName().equals("Archived Customer"))
            .findFirst()
            .orElseThrow();
    this.customerService.archive(archived.getId());
    List<Customer> customers = this.customerService.listAll();
    assertEquals(2, customers.size());
    assertTrue(customers.stream().anyMatch(Customer::isDeleted));
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
}
