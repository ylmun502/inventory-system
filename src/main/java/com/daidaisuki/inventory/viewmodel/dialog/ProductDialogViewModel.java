package com.daidaisuki.inventory.viewmodel.dialog;

import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.service.ProductService;
import com.daidaisuki.inventory.ui.validation.ValidationStatus;
import com.daidaisuki.inventory.util.StringCleaner;
import com.daidaisuki.inventory.util.ValidationUtils;
import com.daidaisuki.inventory.viewmodel.base.BaseDialogViewModel;
import java.math.BigDecimal;
import java.sql.SQLException;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductDialogViewModel extends BaseDialogViewModel<Product> {
  private final ProductService productService;
  private final Product product;
  private final ObjectBinding<ValidationStatus> validationStatus;

  public final StringProperty sku = new SimpleStringProperty("");
  public final StringProperty name = new SimpleStringProperty("");
  public final StringProperty category = new SimpleStringProperty("");
  public final StringProperty description = new SimpleStringProperty("");
  public final StringProperty weight = new SimpleStringProperty("");
  public final StringProperty price = new SimpleStringProperty("");
  public final StringProperty unitType = new SimpleStringProperty("");
  public final BooleanProperty isActive = new SimpleBooleanProperty(true);

  // Need to check validations
  public ProductDialogViewModel(ProductService productService, Product productToEdit) {
    this.productService = productService;
    this.product = productToEdit;

    if (productToEdit != null) {
      this.mapModelToProperties(productToEdit);
    } else {
      this.resetProperties();
    }

    this.validationStatus =
        Bindings.createObjectBinding(
            () -> {
              StringBuilder errors = new StringBuilder();
              String cleanSku = StringCleaner.cleanOrNull(this.sku.get());
              String cleanName = StringCleaner.cleanOrNull(this.name.get());
              String cleanCategory = StringCleaner.cleanOrNull(this.category.get());
              String cleanUnitType = StringCleaner.cleanOrNull(this.unitType.get());

              ValidationUtils.isFieldEmpty(cleanSku, "SKU", errors);
              ValidationUtils.isFieldEmpty(cleanName, "Name", errors);
              ValidationUtils.isFieldEmpty(cleanCategory, "Category", errors);
              ValidationUtils.isFieldEmpty(cleanUnitType, "Unit Type", errors);
              ValidationUtils.isNumeric(this.weight.get(), "Weight", errors, true);
              ValidationUtils.isNumeric(this.price.get(), "Price", errors, true);
              return new ValidationStatus(errors.isEmpty(), errors.toString());
            },
            sku,
            name,
            category,
            unitType,
            weight,
            price);
  }

  @Override
  public Product createResult() {
    Product result = this.product == null ? new Product() : this.product;
    result.setSku(this.sku.get());
    result.setName(this.name.get());
    result.setCategory(this.category.get());
    result.setDescription(this.description.get());
    result.setWeight(Integer.parseInt(this.weight.get().isEmpty() ? "0" : weight.get()));
    result.setSellingPrice(new BigDecimal(price.get().isEmpty() ? "0" : price.get()));
    result.setUnitType(this.unitType.get());
    result.setActive(isActive.get());
    return result;
  }

  @Override
  public BooleanBinding isInvalidProperty() {
    return Bindings.createBooleanBinding(() -> !validationStatus.get().isValid(), validationStatus);
  }

  @Override
  protected void resetProperties() {
    this.sku.set("");
    this.name.set("");
    this.category.set("");
    this.description.set("");
    this.weight.set("");
    this.price.set("");
    this.unitType.set("each");
    this.isActive.set(true);
  }

  protected void mapModelToProperties(Product model) {
    this.sku.set(model.getSku());
    this.name.set(model.getName());
    this.category.set(model.getCategory());
    this.description.set(model.getDescription());
    this.weight.set(Integer.toString(model.getWeight()));
    this.price.set(model.getSellingPrice().toPlainString());
    this.unitType.set(model.getUnitType());
    this.isActive.set(model.isActive());
  }

  public BooleanBinding isNewProperty() {
    return Bindings.createBooleanBinding(() -> product == null);
  }

  public Product getProduct() {
    return this.product;
  }

  public ObservableList<String> getAvailableUnitTypes() {
    try {
      return FXCollections.observableArrayList(this.productService.listDistinctUnitTypes());
    } catch (SQLException e) {
      return FXCollections.observableArrayList("each", "piece", "box");
    }
  }
}
