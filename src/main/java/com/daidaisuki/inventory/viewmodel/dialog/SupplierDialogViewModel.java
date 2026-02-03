package com.daidaisuki.inventory.viewmodel.dialog;

import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.viewmodel.base.BaseDialogViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SupplierDialogViewModel extends BaseDialogViewModel<Supplier> {
  private final Supplier supplier;

  public final StringProperty name = new SimpleStringProperty("");
  public final StringProperty shortCode = new SimpleStringProperty("");
  public final StringProperty email = new SimpleStringProperty("");
  public final StringProperty phone = new SimpleStringProperty("");
  public final StringProperty address = new SimpleStringProperty("");

  public SupplierDialogViewModel(Supplier supplier) {
    this.supplier = supplier;
    if (this.supplier != null) {
      this.name.set(supplier.getName());
      this.shortCode.set(supplier.getShortCode());
      this.email.set(supplier.getEmail());
      this.phone.set(supplier.getPhone());
      this.address.set(supplier.getAddress());
    }
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
    return this.name.isEmpty().or(this.shortCode.isEmpty());
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
}
