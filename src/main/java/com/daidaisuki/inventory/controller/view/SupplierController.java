package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.base.controller.BaseCrudController;
import com.daidaisuki.inventory.controller.dialog.SupplierDialogController;
import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.serviceregistry.ServiceRegistry;
import com.daidaisuki.inventory.util.TableCellUtils;
import com.daidaisuki.inventory.util.TableColumnUtils;
import com.daidaisuki.inventory.viewmodel.dialog.SupplierDialogViewModel;
import com.daidaisuki.inventory.viewmodel.view.SupplierViewModel;
import java.util.List;
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

    TableCellUtils.setupStringCells(this.nameCol, this.shortCodeCol, this.emailCol, this.phoneCol);

    TableColumnUtils.bindColumnWidthsByRatio(this.table, List.of(0.25, 0.25, 0.25, 0.25));
  }

  @Override
  protected Supplier showEntityDialog(Supplier supplier) {
    SupplierDialogViewModel dialogViewModel = new SupplierDialogViewModel(supplier);
    return this.getDialogService()
        .showDialog(SupplierDialogController.class, DialogView.SUPPLIER_DIALOG, dialogViewModel);
  }

  @Override
  protected String getArchiveConfirmationMessage(Supplier supplier) {
    return "Are you sure you want to archive " + supplier.getName() + "?";
  }

  @Override
  protected String getRestoreConfirmationMessage(Supplier supplier) {
    return "Are you sure you want to restore " + supplier.getName() + "?";
  }

  @Override
  protected String getPurgeConfirmationMessage(Supplier supplier) {
    return "Are you sure you want to permanently delete " + supplier.getName() + "?";
  }
}
