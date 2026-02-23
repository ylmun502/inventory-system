package com.daidaisuki.inventory.base.controller;

import com.daidaisuki.inventory.ui.dialog.DialogService;
import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.viewmodel.base.BaseListViewModel;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public abstract class BaseCrudController<T, VM extends BaseListViewModel<T>>
    extends BaseTableController<T, VM> {
  @FXML protected Button addButton;
  @FXML protected Button editButton;
  @FXML protected Button deleteButton;
  @FXML protected TextField searchField;
  @FXML protected ToggleButton archiveToggle;
  private DialogService dialogService;

  public BaseCrudController(VM viewModel) {
    super(viewModel);
  }

  protected void initializeBaseCrudController() {
    this.initializeBaseTableController();
  }

  @Override
  protected void bindViewModelProperties() {
    super.bindViewModelProperties();
    this.addButton.disableProperty().bind(this.viewModel.isBusyProperty());
    BooleanBinding nothingSelected = this.viewModel.selectedItemProperty().isNull();
    this.editButton.disableProperty().bind(nothingSelected);
    this.deleteButton.disableProperty().bind(nothingSelected);
    this.viewModel.showArchivedProperty().bind(archiveToggle.selectedProperty());
    this.searchField.textProperty().bindBidirectional(this.viewModel.searchFilterProperty());
  }

  protected DialogService getDialogService() {
    if (dialogService == null) {
      this.dialogService = new DialogService(this.getWindow());
    }
    return dialogService;
  }

  @FXML
  protected void handleAdd() {
    T newItem = showEntityDialog(null);
    if (newItem != null) {
      this.viewModel.add(newItem);
    }
  }

  @FXML
  protected void handleEdit() {
    T selectedItem = this.viewModel.selectedItemProperty().get();
    if (selectedItem != null) {
      T updatedItem = showEntityDialog(selectedItem);
      if (updatedItem != null) {
        this.viewModel.update(updatedItem);
      }
    }
  }

  @FXML
  protected void handleDelete() {
    T selected = this.viewModel.selectedItemProperty().get();
    if (selected != null) {
      String message = getDeleteConfirmationMessage(selected);
      boolean confirmed =
          AlertHelper.showConfirmationAlert(
              this.getWindow(), "Confirm Delete", message, "This item will be deactivated.");
      if (confirmed) {
        this.viewModel.delete(selected);
      }
    }
  }

  protected abstract T showEntityDialog(T item);

  protected String getDeleteConfirmationMessage(T item) {
    return "Are you sure you want to delete this item?";
  }
}
