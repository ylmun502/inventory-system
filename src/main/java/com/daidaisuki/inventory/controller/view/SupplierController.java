package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.base.controller.BaseCrudController;
import com.daidaisuki.inventory.controller.dialog.SupplierDialogController;
import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.serviceregistry.ServiceRegistry;
import com.daidaisuki.inventory.viewmodel.dialog.SupplierDialogViewModel;
import com.daidaisuki.inventory.viewmodel.view.SupplierViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class SupplierController extends BaseCrudController<Supplier, SupplierViewModel> {
  @FXML private TableColumn<Supplier, String> nameCol;
  @FXML private TableColumn<Supplier, String> shortCodeCol;
  @FXML private TableColumn<Supplier, String> emailCol;
  @FXML private TableColumn<Supplier, String> phoneCol;

  public SupplierController(ServiceRegistry registry) {
    super(new SupplierViewModel(registry.getSupplierService()));
  }

  @FXML
  public void initialize() {
    this.setupStaticUI();
    this.initializeBaseCrudController();
  }

  private void setupStaticUI() {
    this.setupMainTableColumns();
  }

  private void setupMainTableColumns() {
    nameCol.setCellValueFactory(celldata -> celldata.getValue().nameProperty());
    shortCodeCol.setCellValueFactory(celldata -> celldata.getValue().shortCodeProperty());
    emailCol.setCellValueFactory(celldata -> celldata.getValue().emailProperty());
    phoneCol.setCellValueFactory(celldata -> celldata.getValue().phoneProperty());
  }

  @Override
  protected Supplier showEntityDialog(Supplier supplier) {
    SupplierDialogViewModel dialogViewModel = new SupplierDialogViewModel(supplier);
    return this.getDialogService()
        .showDialog(SupplierDialogController.class, DialogView.SUPPLIER_DIALOG, dialogViewModel);
  }

  @Override
  protected String getDeleteConfirmationMessage(Supplier supplier) {
    return "Are you sure you want to delete " + supplier.getName() + "?";
  }
}
