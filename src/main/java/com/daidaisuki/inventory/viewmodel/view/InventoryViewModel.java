package com.daidaisuki.inventory.viewmodel.view;

import com.daidaisuki.inventory.model.InventoryTransaction;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.model.StockBatch;
import com.daidaisuki.inventory.model.dto.StockReceiveRequest;
import com.daidaisuki.inventory.service.InventoryService;
import com.daidaisuki.inventory.service.ProductService;
import com.daidaisuki.inventory.service.SupplierService;
import com.daidaisuki.inventory.viewmodel.base.BaseListViewModel;
import java.util.List;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;

public class InventoryViewModel extends BaseListViewModel<Product> {
  private final ProductService productService;
  private final InventoryService inventoryService;
  private final SupplierService supplierService;
  private final ObservableList<StockBatch> selectedProductBatches =
      FXCollections.observableArrayList();
  private final ObservableList<InventoryTransaction> selectedProductTransactions =
      FXCollections.observableArrayList();
  private final StringProperty searchFilter = new SimpleStringProperty();
  private final FilteredList<Product> filteredList;

  private final StringProperty userText = new SimpleStringProperty(this, "userText", "--");
  private final StringProperty barcodeText = new SimpleStringProperty(this, "barcodeText", "--");
  private final StringProperty reorderLevelText =
      new SimpleStringProperty(this, "reorderLevelText", "--");
  private final StringProperty taxText = new SimpleStringProperty(this, "taxText", "--");
  private final StringProperty weightText = new SimpleStringProperty(this, "weightText", "0");
  private final StringProperty unitTypeText = new SimpleStringProperty(this, "unitTypeText", "--");
  private final StringProperty minStockText = new SimpleStringProperty(this, "minStockText", "--");
  private final StringProperty averageUnitCostText =
      new SimpleStringProperty(this, "averageUnitCostText", "$0.00");
  private final StringProperty markupText = new SimpleStringProperty(this, "markupText", "0%");
  private final StringProperty productTotalValueText =
      new SimpleStringProperty(this, "productTotalValueText", "$0.00");

  public InventoryViewModel(
      ProductService productService,
      InventoryService inventoryService,
      SupplierService supplierService) {
    this.productService = productService;
    this.inventoryService = inventoryService;
    this.supplierService = supplierService;
    this.selectedItem.addListener(
        (obs, oldProduct, newProduct) -> {
          if (newProduct != null) {
            refreshDetail(newProduct.getId());
          } else {
            this.selectedProductBatches.clear();
            this.selectedProductTransactions.clear();
            this.clearLabel();
          }
        });
    this.filteredList = new FilteredList<>(this.getDataList(), p -> true);
    this.searchFilter.addListener(
        (obs, oldVal, newVal) -> {
          filteredList.setPredicate(
              product -> {
                if (newVal == null || newVal.isBlank()) {
                  return true;
                }
                String lowerCaseFilter = newVal.toLowerCase();
                if (product.getName().toLowerCase().contains(lowerCaseFilter)) {
                  return true;
                }
                if (product.getSku().toLowerCase().contains(lowerCaseFilter)) {
                  return true;
                }
                if (product.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                  return true;
                }
                return false;
              });
        });
  }

  public ProductService getProductService() {
    return productService;
  }

  public SupplierService getSupplierService() {
    return supplierService;
  }

  private void refreshDetail(int productId) {
    this.isBusy.set(true);
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() throws Exception {
            List<StockBatch> batches = inventoryService.listInventoryByProduct(productId);
            List<InventoryTransaction> transactions =
                inventoryService.getTransactionHistory(productId);
            Platform.runLater(
                () -> {
                  selectedProductBatches.setAll(batches);
                  selectedProductTransactions.setAll(transactions);
                  isBusy.set(false);
                });
            return null;
          }
        };
    task.setOnFailed(
        e -> {
          isBusy.set(false);
          task.getException().printStackTrace();
        });
    new Thread(task).start();
  }

  private void clearLabel() {
    this.barcodeText.set("--");
    this.reorderLevelText.set("--");
    this.taxText.set("--");
    this.weightText.set("--");
    this.unitTypeText.set("--");
    this.minStockText.set("--");
    this.averageUnitCostText.set("$0.00");
    this.markupText.set("0%");
    this.productTotalValueText.set("$0.00");
  }

  public final ObservableList<StockBatch> getSelectedProductBatches() {
    return selectedProductBatches;
  }

  public final ObservableList<InventoryTransaction> getSelectedProductTransactions() {
    return selectedProductTransactions;
  }

  public final FilteredList<Product> getFilteredList() {
    return filteredList;
  }

  public final StringProperty searchFilterProperty() {
    return searchFilter;
  }

  @Override
  protected List<Product> fetchItems() throws Exception {
    return productService.listProducts();
  }

  @Override
  public void add(Product item) {
    this.runInventoryTask(
        () -> {
          productService.createProduct(item);
          return null;
        });
  }

  @Override
  public void update(Product item) {
    this.runInventoryTask(
        () -> {
          productService.updateProduct(item);
          return null;
        });
  }

  @Override
  public void delete(Product item) {
    this.runInventoryTask(
        () -> {
          productService.removeProduct(item.getId());
          return null;
        });
  }

  public void receiveStock(StockReceiveRequest receiveRequest, int userId) {
    this.runInventoryTask(
        () -> {
          inventoryService.receiveNewStock(receiveRequest, userId);
          return null;
        });
  }

  private void runInventoryTask(Callable<Void> action) {
    this.isBusy.set(true);
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() throws Exception {
            action.call();
            return null;
          }
        };
    task.setOnSucceeded(
        e -> {
          this.refresh();
          if (selectedItem.get() != null) {
            refreshDetail(selectedItem.get().getId());
          }
          this.isBusy.set(false);
        });
    task.setOnFailed(
        e -> {
          this.isBusy.set(false);
          task.getException().printStackTrace();
        });
    new Thread(task).start();
  }
}
