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

    this.validationStatus = Bindings.createObjectBinding(this::validate, this.name, this.shortCode);
  }

  private ValidationStatus validate() {
    StringBuilder errors = new StringBuilder();
    String cleanName = StringCleaner.cleanOrNull(this.name.get());
    String cleanShortCode = StringCleaner.cleanOrNull(this.shortCode.get());

    ValidationUtils.isFieldEmpty(cleanName, "Name", errors);
    ValidationUtils.isFieldEmpty(cleanShortCode, "Short Code", errors);
    return new ValidationStatus(errors.isEmpty(), errors.toString());
  }

  @Override
  public Supplier createResult() {
    String cleanName = StringCleaner.cleanOrNull(this.name.get());
    String cleanShortCode = StringCleaner.cleanOrNull(this.shortCode.get());
    String cleanEmail = StringCleaner.cleanOrNull(this.email.get());
    String cleanPhone = StringCleaner.cleanOrNull(this.phone.get());
    String cleanAddress = StringCleaner.cleanOrNull(this.address.get());
    if (this.supplier == null) {
      Supplier result = new Supplier();
      result.setName(cleanName);
      result.setShortCode(cleanShortCode);
      result.setEmail(cleanEmail);
      result.setPhone(cleanPhone);
      result.setAddress(cleanAddress);
      return result;
    }
    return new Supplier(
        this.supplier.getId(),
        cleanName,
        cleanShortCode,
        cleanEmail,
        cleanPhone,
        cleanAddress,
        this.supplier.getCreatedAt(),
        this.supplier.getUpdatedAt(),
        this.supplier.isDeleted());
  }

  @Override
  public BooleanBinding isInvalidProperty() {
    return Bindings.createBooleanBinding(() -> !validationStatus.get().isValid(), validationStatus);
  }

  @Override
  public BooleanBinding isNewProperty() {
    return Bindings.createBooleanBinding(() -> this.supplier == null);
  }

  @Override
  public void resetProperties() {
    this.name.set("");
    this.shortCode.set("");
    this.email.set("");
    this.phone.set("");
    this.address.set("");
  }

  protected void mapModelToProperties(Supplier supplier) {
    this.name.set(supplier.getName());
    this.shortCode.set(supplier.getShortCode());
    this.email.set(supplier.getEmail());
    this.phone.set(supplier.getPhone());
    this.address.set(supplier.getAddress());
  }
}
