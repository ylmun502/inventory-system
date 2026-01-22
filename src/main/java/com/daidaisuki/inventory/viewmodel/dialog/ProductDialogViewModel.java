package com.daidaisuki.inventory.viewmodel.dialog;

import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.service.ProductService;
import com.daidaisuki.inventory.ui.validation.ValidationStatus;
import com.daidaisuki.inventory.util.StringCleaner;
import com.daidaisuki.inventory.util.ValidationUtils;
import com.daidaisuki.inventory.viewmodel.base.BaseDialogViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProductDialogViewModel extends BaseDialogViewModel<Product> {
  private final ProductService productService;
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
  public ProductDialogViewModel(ProductService productService) {
    this.productService = productService;
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
  protected ObjectBinding<ValidationStatus> validationStatusProperty() {
    return this.validationStatus;
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

  @Override
  protected void mapModelToProperties() {
    if (this.model != null) {
      this.sku.set(this.model.getSku());
      this.name.set(this.model.getName());
      this.category.set(this.model.getCategory());
      this.description.set(this.model.getDescription());
      this.price.set(String.valueOf(this.model.getSellingPriceCents()));
      this.stock.set(String.valueOf(this.model.getCurrentStock()));
      this.isActive.set(this.model.isActive());
    }
  }

  @Override
  protected Product mapPropertiesToModel() {
    if (this.model == null) {
      this.model = new Product();
    }
    this.model.setSku(sku.get());
    this.model.setName(name.get());
    this.model.setCategory(category.get());
    this.model.setDescription(description.get());
    this.model.setWeight(Integer.parseInt(weight.get()));
    this.model.setSellingPriceCents(Long.parseLong(price.get()));
    this.model.setCurrentStock(Integer.parseInt(stock.get()));
    this.model.setActive(isActive.get());
    return this.model;
  }

  @Override
  public void save() throws Exception {
    Product product = mapPropertiesToModel();
    if (this.isNew()) {
      this.productService.createProduct(product);
    } else {
      this.productService.updateProduct(product);
    }
  }
}
