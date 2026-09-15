package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
<<<<<<< HEAD
=======
import com.daidaisuki.inventory.exception.DataAccessException;
>>>>>>> origin/new
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
<<<<<<< HEAD
=======
  private static final String TABLE_NAME = "products";
  private static final String BASE_SELECT_PRODUCT =
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
        average_unit_cost_cents,
        is_active,
        created_at,
        updated_at,
        is_deleted
      FROM products
      """;
  private static final String ORDER_BY_NAME = " ORDER BY name ASC";

>>>>>>> origin/new
  public ProductDAO(Connection connection) {
    super(connection);
  }

<<<<<<< HEAD
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
=======
  public Product save(Product product) {
>>>>>>> origin/new
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
<<<<<<< HEAD
=======
          average_unit_cost_cents,
>>>>>>> origin/new
          is_active,
          created_at,
          updated_at,
          is_deleted)
        VALUES(
<<<<<<< HEAD
          ?, ?, ?, ?, ?, ?, ?, ?,
          ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
=======
          ?, ?, ?, ?, ?, ?, ?, ?, ?,
          ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    String nowString = now.toString();
>>>>>>> origin/new
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
<<<<<<< HEAD
=======
                product.getAverageUnitCost(),
>>>>>>> origin/new
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
<<<<<<< HEAD
        1,
        now,
        now,
        0);
  }

  public void update(Product product) throws SQLException {
=======
        CurrencyUtil.bigDecimalToLong(product.getAverageUnitCost()),
        1,
        nowString,
        nowString,
        0);
  }

  public void update(Product product) {
>>>>>>> origin/new
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
<<<<<<< HEAD
=======
          average_unit_cost_cents = ?,
>>>>>>> origin/new
          is_active = ?,
          updated_at = ?
        WHERE id = ?
        """;
<<<<<<< HEAD
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
=======
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
        CurrencyUtil.bigDecimalToLong(product.getAverageUnitCost()),
        product.isActive() ? 1 : 0,
        OffsetDateTime.now(ZoneOffset.UTC),
        product.getId());
  }

  public void archive(int productId) {
    this.setDeletionStatus(TABLE_NAME, productId, true);
  }

  public void restore(int productId) {
    this.setDeletionStatus(TABLE_NAME, productId, false);
  }

  public void remove(int productId) {
    this.deleteById(TABLE_NAME, productId);
  }

  public Optional<Product> findById(int id) {
    String sql = BASE_SELECT_PRODUCT + " WHERE id = ?";
    return this.queryForObject(sql, this::mapResultSetToProduct, id);
  }

  public List<Product> findAll() {
    String sql = BASE_SELECT_PRODUCT + ORDER_BY_NAME;
    return this.query(sql, this::mapResultSetToProduct);
  }

  public boolean updateStockTotal(int productId, int changeAmount) {
>>>>>>> origin/new
    String sql =
        """
        UPDATE products
        SET current_stock = current_stock + ?, updated_at = ?
        WHERE id = ? AND is_deleted = 0 AND current_stock + ? >= 0
        """;
<<<<<<< HEAD
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
    String sql = "SELECT COUNT(*) FROM suppliers WHERE barcode = ? AND is_deleted = 0";
    return queryForObject(sql, rs -> rs.getInt(1) > 0, barcode).orElse(false);
  }

  private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    try {
=======
    return this.updateReturningAffectedRows(
            sql, changeAmount, OffsetDateTime.now(ZoneOffset.UTC), productId, changeAmount)
        > 0;
  }

  public boolean updateStockTotalAndCost(
      int productId, int changeAmount, BigDecimal newAverageUnitCost) {
    long newAverageUnitCostCents = CurrencyUtil.bigDecimalToLong(newAverageUnitCost);
    String sql =
        """
        UPDATE products
        SET current_stock = current_stock + ?, average_unit_cost_cents = ?, updated_at = ?
        WHERE id = ? AND is_deleted = 0 AND current_stock + ? >= 0
        """;
    return this.updateReturningAffectedRows(
            sql,
            changeAmount,
            newAverageUnitCostCents,
            OffsetDateTime.now(ZoneOffset.UTC),
            productId,
            changeAmount)
        > 0;
  }

  public List<String> findAllDistinctUnitTypes() {
    String sql = "SELECT DISTINCT unit_type FROM products";
    return this.query(sql, this::mapResultSetToUnitType);
  }

  public boolean exists(int productId) {
    String sql = "SELECT 1 FROM products WHERE id = ? AND is_deleted = 0";
    Optional<Integer> result = this.queryForObject(sql, rs -> rs.getInt(1), productId);
    return result.isPresent();
  }

  public boolean existsBySku(String sku) {
    String sql = "SELECT COUNT(*) FROM products WHERE sku = ? AND is_deleted = 0";
    return this.queryForObject(sql, rs -> rs.getInt(1) > 0, sku).orElse(false);
  }

  public boolean existsByBarcode(String barcode) {
    String sql = "SELECT COUNT(*) FROM products WHERE barcode = ? AND is_deleted = 0";
    return this.queryForObject(sql, rs -> rs.getInt(1) > 0, barcode).orElse(false);
  }

  private String mapResultSetToUnitType(ResultSet rs) {
    try {
      return rs.getString("unit_type");
    } catch (SQLException e) {
      throw new DataAccessException("Mapping failed.", e);
    }
  }

  private Product mapResultSetToProduct(ResultSet rs) {
    try {
      int id = rs.getInt("id");
>>>>>>> origin/new
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
<<<<<<< HEAD
=======
      BigDecimal averageUnitCost =
          CurrencyUtil.longToBigDecimal(rs.getLong("average_unit_cost_cents"));
>>>>>>> origin/new
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
<<<<<<< HEAD
=======
          averageUnitCost,
>>>>>>> origin/new
          isActive,
          createdAt,
          updatedAt,
          isDeleted);
<<<<<<< HEAD
    } catch (Exception e) {
      throw new SQLException("Mapping failed for Product ID: " + id, e);
=======
    } catch (SQLException e) {
      throw new DataAccessException("Mapping failed.", e);
>>>>>>> origin/new
    }
  }
}
