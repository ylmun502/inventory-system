package com.daidaisuki.inventory.viewmodel.base;

import javafx.beans.binding.BooleanBinding;

public abstract class BaseDialogViewModel<R> {
  public abstract R createResult();

  public abstract BooleanBinding isInvalidProperty();

  public abstract BooleanBinding isNewProperty();

  protected abstract void mapModelToProperties(R model);

  protected abstract void resetProperties();
}
