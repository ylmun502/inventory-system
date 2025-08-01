package com.daidaisuki.inventory.dao;

import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.db.DatabaseManager;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerDAO {
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                Customer customer = new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone_number"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("platform"));
                customers.add(customer);
            }
        }
        return customers;
    }

    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers(name, phone_number, email, address, platform) VALUES( ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getPlatform());
            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }
            try(ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    customer.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        } 
    }

    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET name = ?, phone_number = ?, email = ?, address = ?, platform = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, customerId);
                stmt.executeUpdate();
            }
    }
    
    public void enrichCustomerStats(Customer customer) throws SQLException {
        String sql = "SELECT COUNT(*) AS total_orders, " + 
                     "SUM(total_amount) AS total_spent, " + 
                     "SUM(discount_amount) AS total_discount " +
                     "FROM orders WHERE customer_id = ?";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customer.getId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                customer.setTotalOrders(rs.getInt("total_orders"));
                customer.setTotalSpent(rs.getDouble("total_spent"));
                customer.setTotalDiscount(rs.getDouble("total_discount"));
            }
        }
    }

    public Customer getById(int id) throws SQLException {
        Customer customer = null;
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    customer = new Customer(rs.getInt("id"),
                                            rs.getString("name"),
                                            rs.getString("phone_number"),
                                            rs.getString("email"),
                                            rs.getString("address"),
                                            rs.getString("platform"));
                }
            }
        }
        return customer;
    }

    public Customer findByName(String name) throws SQLException {
        Customer customer = null;
        String sql = "SELECT * FROM customers WHERE name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    customer = new Customer(rs.getInt("id"),
                                            rs.getString("name"),
                                            rs.getString("phone_number"),
                                            rs.getString("email"),
                                            rs.getString("address"),
                                            rs.getString("platform"));
                }
            }
        }
        return customer;
    }
}
