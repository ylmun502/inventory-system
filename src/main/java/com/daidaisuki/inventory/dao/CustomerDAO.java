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
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                String address = rs.getString("address");
                String platform = rs.getString("platform");
                Customer customer = new Customer(id, name, phoneNumber, email, address, platform);
                customers.add(customer);
            }
        }
        return customers;
    }

    public void addCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO customers(name, phone_number, email, address, platform) VALUES( ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getName());
            stmt.setString(2, c.getPhoneNumber());
            stmt.setString(3, c.getEmail());
            stmt.setString(4, c.getAddress());
            stmt.setString(5, c.getPlatform());
            stmt.executeUpdate();
        } 
    }

    public void updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE customers SET name = ?, phone_number = ?, email = ?, address = ?, platform = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getName());
            stmt.setString(2, c.getPhoneNumber());
            stmt.setString(3, c.getEmail());
            stmt.setString(4, c.getAddress());
            stmt.setInt(5, c.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteCustomer(Customer c) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, c.getId());
                stmt.executeUpdate();
            }
    }
}
