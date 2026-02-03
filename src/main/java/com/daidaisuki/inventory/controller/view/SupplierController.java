package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.base.controller.BaseTableController;
import com.daidaisuki.inventory.controller.dialog.SupplierDialogController;
import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.serviceregistry.ServiceRegistry;
import com.daidaisuki.inventory.viewmodel.dialog.SupplierDialogViewModel;
import com.daidaisuki.inventory.viewmodel.view.SupplierViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class SupplierController extends BaseTableController<Supplier, SupplierViewModel> {
  @FXML private TableColumn<Supplier, String> nameCol;
  @FXML private TableColumn<Supplier, String> shortCodeCol;
  @FXML private TableColumn<Supplier, String> emailCol;
  @FXML private TableColumn<Supplier, String> phoneCol;

  public SupplierController(ServiceRegistry registry) {
    super(new SupplierViewModel(registry.getSupplierService()));
  }

  @FXML
  public void initialize() {
    nameCol.setCellValueFactory(celldata -> celldata.getValue().namProperty());
    shortCodeCol.setCellValueFactory(celldata -> celldata.getValue().shortCodeProperty());
    emailCol.setCellValueFactory(celldata -> celldata.getValue().emailProperty());
    phoneCol.setCellValueFactory(celldata -> celldata.getValue().phoneProperty());
    this.initializeBase();
  }

  @FXML
  protected void handleAdd() {
    SupplierDialogViewModel dialogViewModel = new SupplierDialogViewModel(null);
    Supplier supplier =
        this.getDialogService()
            .showDialog(
                SupplierDialogController.class, DialogView.SUPPLIER_DIALOG, dialogViewModel);
    if (supplier != null) {
      this.viewModel.add(supplier);
    }
  }

  @FXML
  protected void handleEdit() {
    Supplier selected = this.viewModel.selectedItemProperty().get();
    if (selected != null) {
      SupplierDialogViewModel dialogViewModel = new SupplierDialogViewModel(selected);
      Supplier updated =
          this.getDialogService()
              .showDialog(
                  SupplierDialogController.class, DialogView.SUPPLIER_DIALOG, dialogViewModel);
      if (updated != null) {
        this.viewModel.update(updated);
      }
    }
  }

  @FXML
  protected void handleDelete() {
    Supplier selected = this.viewModel.selectedItemProperty().get();
    if (selected != null) {
      this.viewModel.delete(selected);
    }
  }
}
