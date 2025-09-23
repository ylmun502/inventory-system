package com.daidaisuki.inventory.dao;

import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.db.DatabaseManager;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductDAO {
    private final Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }
    
    public List<Product> getAllProducts() throws SQLException {
        String sql = "SELECT * FROM products";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            return products; 
        }
    }

    public void addProduct(Product product) throws SQLException {
        // When inserting
        //"INSERT INTO products(name, ..., last_modified, sync_status) VALUES (?, ..., CURRENT_TIMESTAMP, 'PENDING')"

        String sql = "INSERT INTO products(name, category, stock, selling_price, purchase_cost, shipping_cost) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setInt(3, product.getStock());
            stmt.setDouble(4, product.getPrice());
            stmt.setDouble(5, product.getCost());
            stmt.setDouble(6, product.getShipping());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    product.setId(rs.getInt(1));
                }
            }
        } 
    }

    public void updateProduct(Product product) throws SQLException {
        // When updating
        //"UPDATE products SET ..., last_modified = CURRENT_TIMESTAMP, sync_status = 'PENDING' WHERE id = ?"

        String sql = "UPDATE products SET name = ?, category = ?, stock = ?, selling_price = ?, purchase_cost = ?, shipping_cost = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setInt(3, product.getStock());
            stmt.setDouble(4, product.getPrice());
            stmt.setDouble(5, product.getCost());
            stmt.setDouble(6, product.getShipping());
            stmt.setInt(7, product.getId());
            stmt.executeUpdate();
        } 
    }

    public void deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        } 
    }

    public void decrementStock(int productId, int amount) throws SQLException {
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }

    public Product getById(int id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }
        return null;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(rs.getInt("id"),
                           rs.getString("name"),
                           rs.getString("category"),
                           rs.getInt("stock"),
                           rs.getDouble("selling_price"),
                           rs.getDouble("purchase_cost"),
                           rs.getDouble("shipping_cost"));
    }
}
