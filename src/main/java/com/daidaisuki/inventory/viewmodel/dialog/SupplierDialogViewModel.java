package com.daidaisuki.inventory.viewmodel.dialog;

import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.ui.validation.ValidationStatus;
import com.daidaisuki.inventory.util.StringCleaner;
import com.daidaisuki.inventory.util.ValidationUtils;
import com.daidaisuki.inventory.viewmodel.base.BaseDialogViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SupplierDialogViewModel extends BaseDialogViewModel<Supplier> {
  private final Supplier supplier;
  private final ObjectBinding<ValidationStatus> validationStatus;

  public final StringProperty name = new SimpleStringProperty("");
  public final StringProperty shortCode = new SimpleStringProperty("");
  public final StringProperty email = new SimpleStringProperty("");
  public final StringProperty phone = new SimpleStringProperty("");
  public final StringProperty address = new SimpleStringProperty("");

  public SupplierDialogViewModel(Supplier supplierToEdit) {
    this.supplier = supplierToEdit;
    if (supplierToEdit != null) {
      this.mapModelToProperties(supplierToEdit);
    } else {
      this.resetProperties();
    }

    this.validationStatus =
        Bindings.createObjectBinding(
            () -> {
              StringBuilder errors = new StringBuilder();
              String cleanName = StringCleaner.cleanOrNull(this.name.get());
              String cleanShortCode = StringCleaner.cleanOrNull(this.shortCode.get());

              ValidationUtils.isFieldEmpty(cleanName, "Name", errors);
              ValidationUtils.isFieldEmpty(cleanShortCode, "Short Code", errors);
              return new ValidationStatus(errors.isEmpty(), errors.toString());
            },
            name,
            shortCode);
  }

  @Override
  public Supplier createResult() {
    Supplier result = this.supplier == null ? new Supplier() : this.supplier;
    result.setName(this.name.get());
    result.setShortCode(this.shortCode.get());
    result.setEmail(this.email.get());
    result.setPhone(this.phone.get());
    result.setAddress(this.address.get());
    return result;
  }

  @Override
  public BooleanBinding isInvalidProperty() {
    return Bindings.createBooleanBinding(() -> !validationStatus.get().isValid(), validationStatus);
  }

  @Override
  public BooleanBinding isNewProperty() {
    return Bindings.createBooleanBinding(() -> this.supplier == null);
  }

  protected void mapModelToProperties(Supplier supplier) {
    this.name.set(supplier.getName());
    this.shortCode.set(supplier.getShortCode());
    this.email.set(supplier.getEmail());
    this.phone.set(supplier.getPhone());
    this.address.set(supplier.getAddress());
  }

  @Override
  public void resetProperties() {
    this.name.set("");
    this.shortCode.set("");
    this.email.set("");
    this.phone.set("");
    this.address.set("");
  }
}
