package com.daidaisuki.inventory.dao;

import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.model.Order;
import com.daidaisuki.inventory.db.DatabaseManager;

import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.sql.ResultSet;

public class OrderDAO {
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        CustomerDAO customerDAO = new CustomerDAO();
        try(Connection conn = DatabaseManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()) {
                    String orderDateStr = rs.getString("order_date");
                    LocalDate orderDate = null;
                    if(orderDateStr != null && !orderDateStr.isEmpty()) {
                        orderDate = LocalDate.parse(orderDateStr);
                    }
                    int customerId = rs.getInt("customer_id");
                    Order order = new Order(
                        rs.getInt("id"),
                        customerId,
                        orderDate,
                        rs.getInt("total_items"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("discount_amount"),
                        rs.getString("payment_method"));
                    Customer customer = customerDAO.getById(customerId);
                    order.setCustomer(customer);
                    orders.add(order);
                }
            }
        return orders;
    }

    public void addOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders(customer_id, order_date, total_items, total_amount, discount_amount, payment_method) VALUES(?, ?, ?, ?, ?, ?)";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, order.getCustomerId());
            if (order.getDate() != null) {
                stmt.setString(2, order.getDate().toString());
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }
            stmt.setInt(3, order.getTotalItems());
            stmt.setDouble(4, order.getTotalAmount());
            stmt.setDouble(5, order.getDiscountAmount());
            stmt.setString(6, order.getPaymentMethod());
            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) {
                throw new SQLException("Creating order failed, no row affected.");
            }
            try(ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    order.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        }
    }

    public void updateOrder(Order order) throws SQLException {
        String sql = "UPDATE orders SET customer_id = ?, order_date = ?, total_items = ?, total_amount = ?, discount_amount = ?, payment_method = ? WHERE id = ?";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, order.getCustomerId());
            if (order.getDate() != null) {
                stmt.setDate(2, java.sql.Date.valueOf(order.getDate()));
            } else {
                stmt.setNull(2, java.sql.Types.DATE);
            }
            stmt.setInt(3, order.getTotalItems());
            stmt.setDouble(4, order.getTotalAmount());
            stmt.setDouble(5, order.getDiscountAmount());
            stmt.setString(6, order.getPaymentMethod());
            stmt.setInt(7, order.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteOrder(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
    }
}
