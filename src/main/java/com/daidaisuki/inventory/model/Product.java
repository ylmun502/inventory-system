package com.daidaisuki.inventory.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a product in the inventory system.
 *
 * <p>This class uses JavaFX {@code Property} types to support UI data binding, enabling seamless
 * synchronization with UI controls such as TableView columns or form fields.
 */
public class Product {
  private final IntegerProperty id = new SimpleIntegerProperty(this, "id", -1);
  private final StringProperty name = new SimpleStringProperty(this, "name", "");
  private final StringProperty category = new SimpleStringProperty(this, "category", "");
  private final IntegerProperty stock = new SimpleIntegerProperty(this, "stock", 0);
  private final DoubleProperty price = new SimpleDoubleProperty(this, "price", 0.0);
  private final DoubleProperty cost = new SimpleDoubleProperty(this, "cost", 0.0);
  private final DoubleProperty shipping = new SimpleDoubleProperty(this, "shipping", 0.0);

  /**
   * Creates an empty product with default placeholder values to ensure properties are non-null and
   * ready for binding.
   */
  public Product() {}

  /**
   * Constructs a product with the given details.
   *
   * @param id the product ID
   * @param name the product name
   * @param category the product category
   * @param stock the quantity in stock
   * @param price the selling price
   * @param cost the purchase price
   * @param shipping the shipping cost
   */
  public Product(
      int id, String name, String category, int stock, double price, double cost, double shipping) {
    this.id.set(id);
    this.name.set(name);
    this.category.set(category);
    this.stock.set(stock);
    this.price.set(price);
    this.cost.set(cost);
    this.shipping.set(shipping);
  }

  /**
   * Returns the product ID.
   *
   * @return the product ID
   */
  public int getId() {
    return this.id.get();
  }

  /**
   * Sets the product ID.
   *
   * @param id the new product ID
   */
  public void setId(int id) {
    this.id.set(id);
  }

  /**
   * Returns the id property for UI binding.
   *
   * @return the id property
   */
  public IntegerProperty idProperty() {
    return this.id;
  }

  /**
   * Returns the product name.
   *
   * @return the product name
   */
  public String getName() {
    return this.name.get();
  }

  /**
   * Sets the product name.
   *
   * @param value the new product name
   */
  public void setName(String value) {
    this.name.set(value);
  }

  /**
   * Returns the product name property for UI binding.
   *
   * @return the name property
   */
  public StringProperty nameProperty() {
    return this.name;
  }

  /**
   * Returns the product category.
   *
   * @return the category
   */
  public String getCategory() {
    return this.category.get();
  }

  /**
   * Sets the product category.
   *
   * @param value the new category
   */
  public void setCategory(String value) {
    this.category.set(value);
  }

  /**
   * Returns the product category property for UI binding.
   *
   * @return the category property
   */
  public StringProperty categoryProperty() {
    return this.category;
  }

  /**
   * Returns the current stock quantity.
   *
   * @return the stock count
   */
  public int getStock() {
    return this.stock.get();
  }

  /**
   * Sets the stock quantity.
   *
   * @param value the new stock count
   */
  public void setStock(int value) {
    this.stock.set(value);
  }

  /**
   * Returns the stock property for UI binding.
   *
   * @return the stock property
   */
  public IntegerProperty stockProperty() {
    return this.stock;
  }

  /**
   * Returns the selling price.
   *
   * @return the price
   */
  public double getPrice() {
    return this.price.get();
  }

  /**
   * Sets the selling price.
   *
   * @param value the new price
   */
  public void setPrice(double value) {
    this.price.set(value);
  }

  /**
   * Returns the price property for UI binding.
   *
   * @return the price property
   */
  public DoubleProperty priceProperty() {
    return this.price;
  }

  /**
   * Returns the purchase cost.
   *
   * @return the cost
   */
  public double getCost() {
    return this.cost.get();
  }

  /**
   * Sets the purchase cost.
   *
   * @param value the new cost
   */
  public void setCost(double value) {
    this.cost.set(value);
  }

  /**
   * Returns the cost property for UI binding.
   *
   * @return the cost property
   */
  public DoubleProperty costProperty() {
    return this.cost;
  }

  /**
   * Returns the shipping cost.
   *
   * @return the shipping cost
   */
  public double getShipping() {
    return this.shipping.get();
  }

  /**
   * Sets the shipping cost.
   *
   * @param value the new shipping cost
   */
  public void setShipping(double value) {
    this.shipping.set(value);
  }

  /**
   * Returns the shipping cost property for UI binding.
   *
   * @return the shipping property
   */
  public DoubleProperty shippingProperty() {
    return this.shipping;
  }

  /**
   * Returns {@code true} if the product has at least one item in stock.
   *
   * @return {@code true} if stock > 0; otherwise {@code false}
   */
  public boolean isInStock() {
    return getStock() > 0;
  }

  /**
   * Returns the product name for display in UI components such as ComboBoxes. Overriding {@code
   * toString()} ensures the product name is shown instead of the default object representation.
   *
   * @return the product name
   */
  @Override
  public String toString() {
    return getName();
  }
}
