package com.daidaisuki.inventory.viewmodel.dialog;

import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.ui.validation.ValidationStatus;
import com.daidaisuki.inventory.util.CurrencyUtil;
import com.daidaisuki.inventory.util.StringCleaner;
import com.daidaisuki.inventory.util.ValidationUtils;
import com.daidaisuki.inventory.viewmodel.base.BaseDialogViewModel;
import java.math.BigDecimal;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProductDialogViewModel extends BaseDialogViewModel<Product> {
  private final Product product;
  private final ObjectBinding<ValidationStatus> validationStatus;

  public final StringProperty sku = new SimpleStringProperty("");
  public final StringProperty name = new SimpleStringProperty("");
  public final StringProperty category = new SimpleStringProperty("");
  public final StringProperty description = new SimpleStringProperty("");
  public final StringProperty weight = new SimpleStringProperty("");
  public final StringProperty price = new SimpleStringProperty("");
  public final StringProperty stock = new SimpleStringProperty("");
  public final BooleanProperty isActive = new SimpleBooleanProperty(true);

  // Need to check validations
  public ProductDialogViewModel(Product productToEdit) {
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

              ValidationUtils.isFieldEmpty(cleanSku, "SKU", errors);
              ValidationUtils.isFieldEmpty(cleanName, "Name", errors);
              ValidationUtils.isFieldEmpty(cleanCategory, "Category", errors);
              ValidationUtils.isNumeric(this.weight.get(), "Weight", errors, true);
              ValidationUtils.isNumeric(this.price.get(), "Price", errors, true);
              ValidationUtils.isNumeric(this.stock.get(), "Stock", errors, false);
              return new ValidationStatus(errors.isEmpty(), errors.toString());
            },
            sku,
            name,
            category,
            description,
            weight,
            price,
            stock);
  }

  @Override
  public Product createResult() {
    Product result = this.product == null ? new Product() : product;
    result.setSku(this.sku.get());
    result.setName(this.name.get());
    result.setCategory(this.category.get());
    result.setDescription(this.description.get());
    result.setWeight(Integer.parseInt(this.weight.get().isEmpty() ? "0" : weight.get()));
    result.setSellingPrice(new BigDecimal(price.get().isEmpty() ? "0" : price.get()));
    result.setCurrentStock(Integer.parseInt(stock.get().isEmpty() ? "0" : stock.get()));
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
    this.stock.set("");
    this.isActive.set(true);
  }

  protected void mapModelToProperties(Product model) {
    this.sku.set(model.getSku());
    this.name.set(model.getName());
    this.category.set(model.getCategory());
    this.description.set(model.getDescription());
    this.price.set(CurrencyUtil.format(model.getSellingPrice()));
    this.stock.set(String.valueOf(model.getCurrentStock()));
    this.isActive.set(model.isActive());
  }

  public BooleanBinding isNewProperty() {
    return Bindings.createBooleanBinding(() -> product == null);
  }
}
