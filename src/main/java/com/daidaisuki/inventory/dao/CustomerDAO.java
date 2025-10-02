package com.daidaisuki.inventory.dao;

import com.daidaisuki.inventory.model.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
  private final Connection connection;

  public CustomerDAO(Connection connection) {
    this.connection = connection;
  }

  public List<Customer> getAllCustomers() throws SQLException {
    List<Customer> customers = new ArrayList<>();
    String sql = "SELECT * FROM customers";
    try (Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        Customer customer = mapResultSetToCustomer(rs);
        customers.add(customer);
      }
    }
    return customers;
  }

  public void addCustomer(Customer customer) throws SQLException {
    String sql =
        "INSERT INTO customers(name, phone_number, email, address, platform) VALUES( ?, ?, ?, ?,"
            + " ?)";
    try (PreparedStatement stmt =
        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, customer.getName());
      stmt.setString(2, customer.getPhoneNumber());
      stmt.setString(3, customer.getEmail());
      stmt.setString(4, customer.getAddress());
      stmt.setString(5, customer.getPlatform());
      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Creating customer failed, no rows affected.");
      }
      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          customer.setId(generatedKeys.getInt(1));
        } else {
          throw new SQLException("Creating customer failed, no ID obtained.");
        }
      }
    }
  }

  public void updateCustomer(Customer customer) throws SQLException {
    String sql =
        "UPDATE customers SET name = ?, phone_number = ?, email = ?, address = ?, platform = ?"
            + " WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, customer.getName());
      stmt.setString(2, customer.getPhoneNumber());
      stmt.setString(3, customer.getEmail());
      stmt.setString(4, customer.getAddress());
      stmt.setString(5, customer.getPlatform());
      stmt.setInt(6, customer.getId());
      stmt.executeUpdate();
    }
  }

  public void deleteCustomer(int customerId) throws SQLException {
    String sql = "DELETE FROM customers WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, customerId);
      stmt.executeUpdate();
    }
  }

  public Customer getById(int id) throws SQLException {
    Customer customer = null;
    String sql = "SELECT * FROM customers WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          customer = mapResultSetToCustomer(rs);
        }
      }
    }
    return customer;
  }

  public Customer findByName(String name) throws SQLException {
    Customer customer = null;
    String sql = "SELECT * FROM customers WHERE name = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, name);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          customer = mapResultSetToCustomer(rs);
        }
      }
    }
    return customer;
  }

  private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
    return new Customer(
        rs.getInt("id"),
        rs.getString("name"),
        rs.getString("phone_number"),
        rs.getString("email"),
        rs.getString("address"),
        rs.getString("platform"));
  }
}
