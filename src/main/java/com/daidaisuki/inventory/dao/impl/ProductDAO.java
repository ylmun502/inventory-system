package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.util.CurrencyUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class ProductDAO extends BaseDAO<Product> {
  public ProductDAO(Connection connection) {
    super(connection);
  }

  public List<Product> findAll() throws SQLException {
    String sql =
        """
        SELECT
          id,
          sku,
          barcode,
          name,
          category,
          unit_type,
          tax_category,
          description,
          weight,
          current_stock,
          min_stock_level,
          max_stock_level,
          reordering_level,
          selling_price_cents,
          is_active,
          created_at,
          updated_at,
          is_deleted
        FROM products
        WHERE is_deleted = 0
        ORDER BY name ASC
        """;
    return query(sql, this::mapResultSetToProduct);
  }

  public Product save(Product product) throws SQLException {
    // Reminder for future columns
    // "INSERT INTO products(name, ..., last_modified, sync_status) VALUES (?, ...,
    // CURRENT_TIMESTAMP, 'PENDING')"

    String sql =
        """
        INSERT INTO products(
          sku,
          barcode,
          name,
          category,
          unit_type,
          tax_category,
          description,
          weight,
          current_stock,
          min_stock_level,
          max_stock_level,
          reordering_level,
          selling_price_cents,
          is_active,
          created_at,
          updated_at,
          is_deleted)
        VALUES(
          ?, ?, ?, ?, ?, ?, ?, ?,
          ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    String nowString = now.toString();
    return insert(
        sql,
        (newId) ->
            new Product(
                newId,
                product.getSku(),
                product.getBarcode(),
                product.getName(),
                product.getCategory(),
                product.getUnitType(),
                product.getTaxCategory(),
                product.getDescription(),
                product.getWeight(),
                0,
                product.getMinStockLevel(),
                product.getMaxStockLevel(),
                product.getReorderingLevel(),
                product.getSellingPrice(),
                true,
                now,
                now,
                false),
        product.getSku(),
        product.getBarcode(),
        product.getName(),
        product.getCategory(),
        product.getUnitType(),
        product.getTaxCategory(),
        product.getDescription(),
        product.getWeight(),
        0,
        product.getMinStockLevel(),
        product.getMaxStockLevel(),
        product.getReorderingLevel(),
        CurrencyUtil.bigDecimalToLong(product.getSellingPrice()),
        1,
        nowString,
        nowString,
        0);
  }

  public void update(Product product) throws SQLException {
    // Reminder for future columns
    // "UPDATE products SET ..., last_modified = CURRENT_TIMESTAMP, sync_status = 'PENDING' WHERE id
    // = ?"

    String sql =
        """
        UPDATE products
        SET
          sku = ?,
          barcode = ?,
          name = ?,
          category = ?,
          unit_type = ?,
          tax_category = ?,
          description = ?,
          weight = ?,
          current_stock = ?,
          min_stock_level = ?,
          max_stock_level = ?,
          reordering_level = ?,
          selling_price_cents = ?,
          is_active = ?,
          updated_at = ?
        WHERE id = ?
        """;
    int affectedRows =
        update(
            sql,
            product.getSku(),
            product.getBarcode(),
            product.getName(),
            product.getCategory(),
            product.getUnitType(),
            product.getTaxCategory(),
            product.getDescription(),
            product.getWeight(),
            product.getCurrentStock(),
            product.getMinStockLevel(),
            product.getMaxStockLevel(),
            product.getReorderingLevel(),
            CurrencyUtil.bigDecimalToLong(product.getSellingPrice()),
            product.isActive() ? 1 : 0,
            OffsetDateTime.now(ZoneOffset.UTC),
            product.getId());
    if (affectedRows == 0) {
      throw new SQLException("Updating product failed, no rows affected.");
    }
  }

  public void delete(int productId) throws SQLException {
    String sql = "UPDATE products SET is_deleted = 1, updated_at = ? WHERE id = ?";
    update(sql, OffsetDateTime.now(ZoneOffset.UTC), productId);
  }

  public void restore(int productId) throws SQLException {
    String sql =
        "UPDATE products SET is_deleted = 0, updated_at = ? WHERE id = ? AND is_deleted = 1";
    update(sql, OffsetDateTime.now(ZoneOffset.UTC), productId);
  }

  public boolean updateStockTotal(int productId, int changeAmount) throws SQLException {
    String sql =
        """
        UPDATE products
        SET current_stock = current_stock + ?, updated_at = ?
        WHERE id = ? AND is_deleted = 0 AND current_stock + ? >= 0
        """;
    return update(sql, changeAmount, OffsetDateTime.now(ZoneOffset.UTC), productId, changeAmount)
        > 0;
  }

  public Optional<Product> findById(int id) throws SQLException {
    String sql =
        """
        SELECT
          id,
          sku,
          barcode,
          name,
          category,
          unit_type,
          tax_category,
          description,
          weight,
          current_stock,
          min_stock_level,
          max_stock_level,
          reordering_level,
          selling_price_cents,
          is_active,
          created_at,
          updated_at,
          is_deleted
        FROM products
        WHERE id = ?
        """;
    return queryForObject(sql, this::mapResultSetToProduct, id);
  }

  public List<String> findAllDistinctUnitTypes() throws SQLException {
    String sql = "SELECT DISTINCT unit_type FROM products";
    return query(sql, this::mapResultSetToUnitType);
  }

  public boolean exists(int productId) throws SQLException {
    String sql = "SELECT 1 FROM products WHERE id = ? AND is_deleted = 0";
    Optional<Integer> result = queryForObject(sql, rs -> rs.getInt(1), productId);
    return result.isPresent();
  }

  public boolean existsBySku(String sku) throws SQLException {
    String sql = "SELECT COUNT(*) FROM products WHERE sku = ? AND is_deleted = 0";
    return queryForObject(sql, rs -> rs.getInt(1) > 0, sku).orElse(false);
  }

  public boolean existsByBarcode(String barcode) throws SQLException {
    String sql = "SELECT COUNT(*) FROM products WHERE barcode = ? AND is_deleted = 0";
    return queryForObject(sql, rs -> rs.getInt(1) > 0, barcode).orElse(false);
  }

  private String mapResultSetToUnitType(ResultSet rs) throws SQLException {
    return rs.getString("unit_type");
  }

  private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    try {
      String sku = rs.getString("sku");
      String barcode = rs.getString("barcode");
      String name = rs.getString("name");
      String category = rs.getString("category");
      String unitType = rs.getString("unit_type");
      String taxCategory = rs.getString("tax_category");
      String description = rs.getString("description");
      int weight = rs.getInt("weight");
      int currentStock = rs.getInt("current_stock");
      int minStockLevel = rs.getInt("min_stock_level");
      int maxStockLevel = rs.getInt("max_stock_level");
      int reorderingLevel = rs.getInt("reordering_level");
      BigDecimal sellingPrice = CurrencyUtil.longToBigDecimal(rs.getLong("selling_price_cents"));
      boolean isActive = rs.getInt("is_active") == 1;
      String createdAtString = rs.getString("created_at");
      String updatedAtString = rs.getString("updated_at");
      OffsetDateTime createdAt = OffsetDateTime.parse(createdAtString);
      OffsetDateTime updatedAt = OffsetDateTime.parse(updatedAtString);
      boolean isDeleted = rs.getInt("is_deleted") == 1;
      return new Product(
          id,
          sku,
          barcode,
          name,
          category,
          unitType,
          taxCategory,
          description,
          weight,
          currentStock,
          minStockLevel,
          maxStockLevel,
          reorderingLevel,
          sellingPrice,
          isActive,
          createdAt,
          updatedAt,
          isDeleted);
    } catch (Exception e) {
      throw new SQLException("Mapping failed for Product ID: " + id, e);
    }
  }
}
