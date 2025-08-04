package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.ProductDAO;
import com.daidaisuki.inventory.model.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public List<Product> getAllProducts() throws SQLException {
        return  productDAO.getAllProducts();
    }

    public void addProduct(Product product) throws SQLException {
        productDAO.addProduct(product);
    }

    public void updateProduct(Product product) throws SQLException {
        productDAO.updateProduct(product);
    }

    public void deleteProduct(int productId) throws SQLException {
        productDAO.deleteProduct(productId);
    }

    public Product getById(int productId) throws SQLException {
        return productDAO.getById(productId);
    }

    public void decrementStock(int productId, int amount) throws SQLException {
        productDAO.decrementStock(productId, amount);
    }
}
