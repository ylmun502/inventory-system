package com.daidaisuki.inventory.dao;

import com.daidaisuki.inventory.db.DatabaseManager;
import com.daidaisuki.inventory.model.OrderItem;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemDAO {
    public void addOrderItem(OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_items(order_id, product_id, quantity, unit_price, cost_at_sale) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getOrderId());
            stmt.setInt(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getUnitPrice());
            stmt.setDouble(5, item.getCostAtSale());
            stmt.executeUpdate();
        }
    }

    public List<OrderItem> getItemsByOrderId(int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");
                double unitPrice = rs.getDouble("unit_price");
                double costAtSale = rs.getDouble("cost_at_sale");
                OrderItem item = new OrderItem(id, orderId, productId, quantity, unitPrice, costAtSale);
                items.add(item);
            }
        }
        return items;
    }
}
