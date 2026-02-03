package com.daidaisuki.inventory.viewmodel.base;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

public abstract class BaseDialogViewModel<R> {
  protected final ReadOnlyBooleanWrapper isBusy = new ReadOnlyBooleanWrapper(false);

  public abstract R createResult();

  public abstract BooleanBinding isInvalidProperty();

  public abstract BooleanBinding isNewProperty();

  // protected abstract void mapModelToProperties(R model);

  protected abstract void resetProperties();

  public ReadOnlyBooleanProperty isBusyProperty() {
    return isBusy.getReadOnlyProperty();
  }
}
