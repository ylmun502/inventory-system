package com.daidaisuki.inventory.viewmodel.base;

import com.daidaisuki.inventory.ui.validation.ValidationStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

public abstract class BaseDialogViewModel<T> {
  protected T model;
  protected final ReadOnlyBooleanWrapper isNew = new ReadOnlyBooleanWrapper(true);

  public void setModel(T model) {
    this.model = model;
    this.isNew.set(model == null);
    resetProperties();
    if (model != null) {
      mapModelToProperties();
    }
  }

  public T getModel() {
    return this.model;
  }

  public boolean isNew() {
    return this.isNew.get();
  }

  public ReadOnlyBooleanProperty isNewProperty() {
    return this.isNew.getReadOnlyProperty();
  }

  public BooleanBinding isInvalidProperty() {
    return Bindings.createBooleanBinding(
        () -> !validationStatusProperty().get().isValid(), validationStatusProperty());
  }

  public String getValidationsErrors() {
    return this.validationStatusProperty().get().errors();
  }

  protected abstract void resetProperties();

  protected abstract void mapModelToProperties();

  protected abstract T mapPropertiesToModel();

  protected abstract ObjectBinding<ValidationStatus> validationStatusProperty();

  public abstract void save() throws Exception;
}
