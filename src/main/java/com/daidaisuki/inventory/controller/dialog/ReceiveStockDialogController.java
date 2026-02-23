package com.daidaisuki.inventory.controller.dialog;

import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.model.dto.StockReceiveRequest;
import com.daidaisuki.inventory.viewmodel.dialog.ReceiveStockDialogViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class ReceiveStockDialogController
    extends BaseDialogController<StockReceiveRequest, ReceiveStockDialogViewModel> {
  @FXML private Label productNameLabel;
  @FXML private ComboBox<Supplier> supplierComboBox;
  @FXML private TextField batchCodeTextField;
  @FXML private TextField quantityTextField;
  @FXML private TextField unitCostTextField;
  @FXML private DatePicker expiryDatePicker;
  @FXML private TextArea reasonTextArea;

  public ReceiveStockDialogController(ReceiveStockDialogViewModel viewModel) {
    super(viewModel);
  }

  @FXML
  public void initialize() {
    this.productNameLabel.setText("Receiving for: " + this.viewModel.getProduct().getName());
    this.supplierComboBox.setItems(this.viewModel.getSuppliers());
    this.supplierComboBox
        .valueProperty()
        .bindBidirectional(this.viewModel.selectedSupplierProperty());
    this.supplierComboBox.setConverter(
        new StringConverter<>() {
          @Override
          public String toString(Supplier supplier) {
            return supplier == null ? "" : supplier.getName();
          }

          @Override
          public Supplier fromString(String string) {
            return null;
          }
        });
    this.batchCodeTextField.textProperty().bindBidirectional(this.viewModel.batchCodeProperty());
    this.quantityTextField.textProperty().bindBidirectional(this.viewModel.quantityProperty());
    this.unitCostTextField.textProperty().bindBidirectional(this.viewModel.unitCostProperty());
    this.expiryDatePicker.valueProperty().bindBidirectional(this.viewModel.expiryDateProperty());
    this.reasonTextArea.textProperty().bindBidirectional(this.viewModel.reasonProperty());
    setupNumericFormatters();
    this.confirmButton.disableProperty().bind(this.viewModel.isInvalidProperty());
  }

  private void setupNumericFormatters() {
    quantityTextField.setTextFormatter(
        new TextFormatter<>(change -> change.getControlText().matches("\\d*") ? change : null));
    unitCostTextField.setTextFormatter(
        new TextFormatter<>(
            change -> change.getControlText().matches("\\d*\\.?\\d{0,2}") ? change : null));
  }

  @FXML
  private void handleGenerateBatchCode() {
    this.viewModel.generateBatchCode();
  }

  @Override
  protected void handleConfirm() {
    if (!this.viewModel.isInvalidProperty().get()) {
      this.confirmed = true;
      this.dialogStage.close();
    }
  }

  @Override
  public StockReceiveRequest getResult() {
    return this.confirmed ? this.viewModel.createResult() : null;
  }
}
