package com.daidaisuki.inventory.viewmodel.view;

import com.daidaisuki.inventory.model.InventoryTransaction;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.model.StockBatch;
import com.daidaisuki.inventory.model.dto.StockReceiveRequest;
import com.daidaisuki.inventory.service.InventoryService;
import com.daidaisuki.inventory.service.ProductService;
import com.daidaisuki.inventory.service.SupplierService;
import com.daidaisuki.inventory.util.CurrencyUtil;
import com.daidaisuki.inventory.util.NumberUtils;
import com.daidaisuki.inventory.viewmodel.base.BaseListViewModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.util.Pair;

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
  private final StringProperty reorderingLevelText =
      new SimpleStringProperty(this, "reorderingLevelText", "--");
  private final StringProperty taxCategoryText =
      new SimpleStringProperty(this, "taxCategoryText", "--");
  private final StringProperty weightText = new SimpleStringProperty(this, "weightText", "0");
  private final StringProperty unitTypeText = new SimpleStringProperty(this, "unitTypeText", "--");
  private final StringProperty minStockLevelText =
      new SimpleStringProperty(this, "minStockLevelText", "--");
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
            this.updatePresentation(newProduct);
            this.refreshDetail(newProduct.getId());
          } else {
            this.selectedProductBatches.clear();
            this.selectedProductTransactions.clear();
            this.clearPresentation();
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
    this.executeLoadingTask(
        () -> {
          List<StockBatch> batches = inventoryService.listInventoryByProduct(productId);
          List<InventoryTransaction> transactions =
              inventoryService.getTransactionHistory(productId);
          return new Pair<>(batches, transactions);
        },
        result -> {
          selectedProductBatches.setAll(result.getKey());
          selectedProductTransactions.setAll(result.getValue());
        });
  }

  private void updatePresentation(Product product) {
    this.barcodeText.set(product.getBarcode());
    this.reorderingLevelText.set(String.valueOf(product.getReorderingLevel()));
    this.taxCategoryText.set(product.getTaxCategory());
    this.weightText.set(String.valueOf(product.getWeight()));
    this.unitTypeText.set(product.getUnitType());
    this.minStockLevelText.set(String.valueOf(product.getMinStockLevel()));
    this.averageUnitCostText.set(CurrencyUtil.format(product.getAverageUnitCost()));
    BigDecimal cost = product.getAverageUnitCost();
    BigDecimal price = product.getSellingPrice();
    if (cost == null || price == null || cost.compareTo(BigDecimal.ZERO) == 0) {
      this.markupText.set("0%");
    } else {
      BigDecimal markup =
          price
              .subtract(cost)
              .divide(cost, 4, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));
      this.markupText.set(NumberUtils.percentage(markup));
    }
    BigDecimal totalValue =
        product.getAverageUnitCost().multiply(BigDecimal.valueOf(product.getCurrentStock()));
    this.productTotalValueText.set(CurrencyUtil.format(totalValue));
  }

  private void clearPresentation() {
    this.barcodeText.set("--");
    this.reorderingLevelText.set("--");
    this.taxCategoryText.set("--");
    this.weightText.set("--");
    this.unitTypeText.set("--");
    this.minStockLevelText.set("--");
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
    this.runInventoryTask(() -> productService.createProduct(item));
  }

  @Override
  public void update(Product item) {
    this.runInventoryTask(() -> productService.updateProduct(item));
  }

  @Override
  public void delete(Product item) {
    this.runInventoryTask(() -> productService.removeProduct(item.getId()));
  }

  public void receiveStock(StockReceiveRequest receiveRequest, int userId) {
    this.runInventoryTask(() -> inventoryService.receiveNewStock(receiveRequest, userId));
  }

  private void runInventoryTask(TaskAction action) {
    this.runAsync(
        action,
        () -> {
          this.refresh();
          if (this.selectedItem.get() != null) {
            this.refreshDetail(this.selectedItem.get().getId());
          }
        });
  }

  public StringProperty userTextProperty() {
    return userText;
  }

  public StringProperty barcodeTextProperty() {
    return barcodeText;
  }

  public StringProperty reorderingLevelTextProperty() {
    return reorderingLevelText;
  }

  public StringProperty taxCategoryTextProperty() {
    return taxCategoryText;
  }

  public StringProperty weightTextProperty() {
    return weightText;
  }

  public StringProperty unitTypeTextProperty() {
    return unitTypeText;
  }

  public StringProperty minStockLevelTextProperty() {
    return minStockLevelText;
  }

  public StringProperty averageUnitCostTextProperty() {
    return averageUnitCostText;
  }

  public StringProperty markupTextProperty() {
    return markupText;
  }

  public StringProperty productTotalValueTextProperty() {
    return productTotalValueText;
  }
}
