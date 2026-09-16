package com.daidaisuki.inventory.base.controller;

import com.daidaisuki.inventory.ui.dialog.DialogService;
import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.viewmodel.base.BaseListViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public abstract class BaseCrudController<T, VM extends BaseListViewModel<T>>
    extends BaseTableController<T, VM> {
  @FXML protected Button addButton;
  @FXML protected Button editButton;
  @FXML protected Button archiveButton;
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
    this.archiveButton.disableProperty().bind(nothingSelected);
    this.addButton.visibleProperty().bind(this.viewModel.showArchivedProperty().not());
    this.addButton.managedProperty().bind(this.addButton.visibleProperty());
    this.editButton
        .textProperty()
        .bind(
            Bindings.when(this.viewModel.showArchivedProperty()).then("Restore").otherwise("Edit"));
    this.archiveButton
        .textProperty()
        .bind(
            Bindings.when(this.viewModel.showArchivedProperty())
                .then("Purge")
                .otherwise("Archive"));
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
    T selected = this.viewModel.selectedItemProperty().get();
    if (selected == null) {
      return;
    }
    if (this.viewModel.showArchivedProperty().get()) {
      this.restoreItem(selected);
      return;
    }
    T updated = showEntityDialog(selected);
    if (updated != null) {
      this.viewModel.update(updated);
    }
  }

  @FXML
  protected void handleArchive() {
    T selected = this.viewModel.selectedItemProperty().get();
    if (selected == null) {
      return;
    }
    if (this.viewModel.showArchivedProperty().get()) {
      this.purgeItem(selected);
      return;
    }
    String message = this.getArchiveConfirmationMessage(selected);
    boolean confirmed =
        AlertHelper.showConfirmationAlert(
            this.getWindow(), "Confirm Archive", message, "This item will be archived.");
    if (confirmed) {
      this.viewModel.archive(selected);
    }
  }

  protected void restoreItem(T item) {
    String message = this.getRestoreConfirmationMessage(item);
    boolean confirmed =
        AlertHelper.showConfirmationAlert(
            this.getWindow(), "Confirm Restore", message, "This item will be restored");
    if (confirmed) {
      this.viewModel.restore(item);
    }
  }

  protected void purgeItem(T item) {
    String message = this.getPurgeConfirmationMessage(item);
    boolean confirmed =
        AlertHelper.showConfirmationAlert(
            this.getWindow(), "DANGER", message, "This action cannot be undone.");
    if (confirmed) {
      this.viewModel.delete(item);
    }
  }

  protected abstract T showEntityDialog(T item);

  protected String getArchiveConfirmationMessage(T item) {
    return "Are you sure you want to archive this item?";
  }

  protected String getRestoreConfirmationMessage(T item) {
    return "Are you sure you want to restore this item?";
  }

  protected String getPurgeConfirmationMessage(T item) {
    return "Are you sure you want to purge this item?";
  }
}
