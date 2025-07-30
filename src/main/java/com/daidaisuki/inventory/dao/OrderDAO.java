package com.daidaisuki.inventory.dao;

import com.daidaisuki.inventory.model.Order;
import com.daidaisuki.inventory.db.DatabaseManager;

import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.sql.ResultSet;

public class OrderDAO {
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try(Connection conn = DatabaseManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()) {
                    Date sqlDate = rs.getDate("order_date");
                    LocalDate orderDate = sqlDate != null ? sqlDate.toLocalDate() : null;
                    Order order = new Order(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        orderDate,
                        rs.getInt("total_items"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("discount_amount"),
                        rs.getString("payment_method"));
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
                stmt.setDate(2, java.sql.Date.valueOf(order.getDate()));
            } else {
                stmt.setNull(2, java.sql.Types.DATE);
            }
            stmt.setInt(3, order.getTotalItems());
            stmt.setDouble(4, order.getTotalAmount());
            stmt.setDouble(5, order.getDiscountAmount());
            stmt.setString(6, order.getPaymentMethod());
            stmt.executeUpdate();
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
