package com.daidaisuki.inventory.model;

import com.daidaisuki.inventory.model.base.BaseModel;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a product in the inventory system.
 *
 * <p>This class uses JavaFX {@code Property} types to support UI data binding, enabling seamless
 * synchronization with UI controls such as TableView columns or form fields.
 */
public class Product extends BaseModel {
  private final StringProperty sku = new SimpleStringProperty(this, "sku", "");
  private final StringProperty barcode = new SimpleStringProperty(this, "barcode", "");
  private final StringProperty name = new SimpleStringProperty(this, "name", "");
  private final StringProperty category = new SimpleStringProperty(this, "category", "");
  private final StringProperty unitType = new SimpleStringProperty(this, "unitType", "each");
  private final StringProperty taxCategory =
      new SimpleStringProperty(this, "taxCategory", "standard");
  private final StringProperty description = new SimpleStringProperty(this, "description", "");
  private final IntegerProperty weight = new SimpleIntegerProperty(this, "weight", 0);
  private final IntegerProperty currentStock = new SimpleIntegerProperty(this, "currentStock", 0);
  private final IntegerProperty minStockLevel = new SimpleIntegerProperty(this, "minStockLevel", 0);
  private final IntegerProperty maxStockLevel = new SimpleIntegerProperty(this, "maxStockLevel", 0);
  private final IntegerProperty reorderingLevel =
      new SimpleIntegerProperty(this, "reorderingLevel", 5);
  private final ObjectProperty<BigDecimal> sellingPrice =
      new SimpleObjectProperty<>(this, "sellingPrice", BigDecimal.ZERO);
  private final ObjectProperty<BigDecimal> averageUnitCost =
      new SimpleObjectProperty<>(this, "averageUnitCost", BigDecimal.ZERO);
  private final BooleanProperty isActive = new SimpleBooleanProperty(this, "isActive", true);
  private final ReadOnlyBooleanWrapper inStock = new ReadOnlyBooleanWrapper(this, "inStock", false);
  private final ReadOnlyStringWrapper stockStatus =
      new ReadOnlyStringWrapper(this, "stockStatus", "OK");

  /**
   * /** Creates an empty product with default placeholder values to ensure properties are non-null
   * and ready for binding.
   */
  public Product() {
    super(-1, OffsetDateTime.now(), OffsetDateTime.now(), false);
    this.initializeBindings();
  }

  /**
   * Constructs a product with the given details.
   *
   * @param id the product ID
   * @param name the product name
   * @param category the product category
   * @param currentStock the quantity in stock
   * @param sellingPriceCents the selling price
   * @param reorderingLevel the reordering level
   * @param isActive the is active
   */
  public Product(
      int id,
      String sku,
      String barcode,
      String name,
      String category,
      String unitType,
      String taxCategory,
      String description,
      int weight,
      int currentStock,
      int minStockLevel,
      int maxStockLevel,
      int reorderingLevel,
      BigDecimal sellingPrice,
      boolean isActive,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt,
      boolean deleted) {
    super(id, createdAt, updatedAt, deleted);
    this.sku.set(sku);
    this.barcode.set(barcode);
    this.name.set(name);
    this.category.set(category);
    this.description.set(description);
    this.weight.set(weight);
    this.currentStock.set(currentStock);
    this.minStockLevel.set(minStockLevel);
    this.maxStockLevel.set(maxStockLevel);
    this.reorderingLevel.set(reorderingLevel);
    this.sellingPrice.set(sellingPrice);
    this.isActive.set(isActive);
    this.initializeBindings();
  }

  private void initializeBindings() {
    this.inStock.bind(currentStock.greaterThan(0));
    this.stockStatus.bind(
        Bindings.createStringBinding(
            () -> {
              int stock = currentStock.get();
              if (stock <= 0) {
                return "OUT";
              }
              if (stock <= minStockLevel.get()) {
                return "CRITICAL";
              }
              if (stock <= reorderingLevel.get()) {
                return "LOW";
              }
              return "OK";
            },
            currentStock,
            minStockLevel,
            reorderingLevel));
  }

  public final String getSku() {
    return this.sku.get();
  }

  public final void setSku(String sku) {
    this.sku.set(sku);
  }

  public final StringProperty skuProperty() {
    return this.sku;
  }

  public final String getBarcode() {
    return this.barcode.get();
  }

  public final void setBarcode(String barcode) {
    this.barcode.set(barcode);
  }

  public final StringProperty barcodeProperty() {
    return this.barcode;
  }

  /**
   * Returns the product name.
   *
   * @return the product name
   */
  public final String getName() {
    return this.name.get();
  }

  /**
   * Sets the product name.
   *
   * @param value the new product name
   */
  public final void setName(String name) {
    this.name.set(name);
  }

  /**
   * Returns the product name property for UI binding.
   *
   * @return the name property
   */
  public final StringProperty nameProperty() {
    return this.name;
  }

  /**
   * Returns the product category.
   *
   * @return the category
   */
  public final String getCategory() {
    return this.category.get();
  }

  /**
   * Sets the product category.
   *
   * @param value the new category
   */
  public final void setCategory(String category) {
    this.category.set(category);
  }

  /**
   * Returns the product category property for UI binding.
   *
   * @return the category property
   */
  public final StringProperty categoryProperty() {
    return this.category;
  }

  public final String getUnitType() {
    return this.unitType.get();
  }

  public final void setUnitType(String unitType) {
    this.unitType.set(unitType);
  }

  public final StringProperty unitTypeProperty() {
    return this.unitType;
  }

  public final String getTaxCategory() {
    return this.taxCategory.get();
  }

  public final void setTaxCategory(String taxCategory) {
    this.taxCategory.set(taxCategory);
  }

  public final StringProperty taxCategoryProperty() {
    return this.taxCategory;
  }

  public final String getDescription() {
    return this.description.get();
  }

  public final void setDescription(String description) {
    this.description.set(description);
  }

  public final StringProperty descriptionProperty() {
    return this.description;
  }

  public final int getWeight() {
    return this.weight.get();
  }

  public final void setWeight(int weight) {
    this.weight.set(weight);
  }

  public final IntegerProperty weightProperty() {
    return this.weight;
  }

  /**
   * Returns the current stock quantity.
   *
   * @return the stock count
   */
  public final int getCurrentStock() {
    return this.currentStock.get();
  }

  /**
   * Sets the stock quantity.
   *
   * @param value the new stock count
   */
  public final void setCurrentStock(int currentStock) {
    this.currentStock.set(currentStock);
  }

  /**
   * Returns the stock property for UI binding.
   *
   * @return the stock property
   */
  public final IntegerProperty currentStockProperty() {
    return this.currentStock;
  }

  public final int getMinStockLevel() {
    return this.minStockLevel.get();
  }

  public final void setMinStockLevel(int minStockLevel) {
    this.minStockLevel.set(minStockLevel);
  }

  public final IntegerProperty minStockLevelProperty() {
    return this.minStockLevel;
  }

  public final int getMaxStockLevel() {
    return this.maxStockLevel.get();
  }

  public final void setMaxStockLevel(int maxStockLevel) {
    this.maxStockLevel.set(maxStockLevel);
  }

  public final IntegerProperty maxStockLevelProperty() {
    return this.maxStockLevel;
  }

  /**
   * Returns the reordering level.
   *
   * @return the reordering level
   */
  public final int getReorderingLevel() {
    return this.reorderingLevel.get();
  }

  /**
   * Sets the reordering level.
   *
   * @param value the reordering level
   */
  public final void setReorderingLevel(int value) {
    this.reorderingLevel.set(value);
  }

  /**
   * Returns the reodering level property for UI binding.
   *
   * @return the reordering level property
   */
  public final IntegerProperty reorderingLevelProperty() {
    return this.reorderingLevel;
  }

  /**
   * Returns the selling price.
   *
   * @return the price
   */
  public final BigDecimal getSellingPrice() {
    return this.sellingPrice.get();
  }

  /**
   * Sets the selling price.
   *
   * @param value the new price
   */
  public final void setSellingPrice(BigDecimal sellingPrice) {
    this.sellingPrice.set(sellingPrice);
  }

  /**
   * Returns the price property for UI binding.
   *
   * @return the price property
   */
  public final ObjectProperty<BigDecimal> sellingPriceProperty() {
    return this.sellingPrice;
  }

  public final BigDecimal getAverageUnitCost() {
    return this.averageUnitCost.get();
  }

  public final void setAverageUnitCost(BigDecimal averageUnitCost) {
    this.averageUnitCost.set(averageUnitCost);
  }

  public final ObjectProperty<BigDecimal> averageUnitCostProperty() {
    return this.averageUnitCost;
  }

  /**
   * Returns the active status of the product.
   *
   * @return true if the product is active, false otherwise
   */
  public final boolean isActive() {
    return this.isActive.get();
  }

  /**
   * Sets the active status of the product.
   *
   * @param value the new is active status
   */
  public final void setActive(boolean value) {
    this.isActive.set(value);
  }

  /**
   * Returns the is active property for UI binding.
   *
   * @return the is active property
   */
  public final BooleanProperty isActiveProperty() {
    return this.isActive;
  }

  /**
   * Returns {@code true} if the product has at least one item in stock. Will update to binding
   * later.
   *
   * @return {@code true} if stock > 0; otherwise {@code false}
   */
  public final boolean isInStock() {
    return this.inStock.get();
  }

  public final ReadOnlyBooleanProperty inStockProperty() {
    return this.inStock.getReadOnlyProperty();
  }

  public final String getStockStatus() {
    return this.stockStatus.get();
  }

  public final ReadOnlyStringProperty stockStatusProperty() {
    return this.stockStatus.getReadOnlyProperty();
  }

  /**
   * Returns the product name for display in UI components such as ComboBoxes. Overriding {@code
   * toString()} ensures the product name is shown instead of the default object representation.
   *
   * @return the product name
   */
  @Override
  public String toString() {
    return this.getName();
  }
}
