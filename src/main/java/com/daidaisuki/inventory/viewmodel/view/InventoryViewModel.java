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
<<<<<<< HEAD
import java.util.List;
=======
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
>>>>>>> origin/new
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
<<<<<<< HEAD
import javafx.collections.transformation.FilteredList;
=======
>>>>>>> origin/new
import javafx.util.Pair;

public class InventoryViewModel extends BaseListViewModel<Product> {
  private final ProductService productService;
  private final InventoryService inventoryService;
  private final SupplierService supplierService;
<<<<<<< HEAD
=======

>>>>>>> origin/new
  private final ObservableList<StockBatch> selectedProductBatches =
      FXCollections.observableArrayList();
  private final ObservableList<InventoryTransaction> selectedProductTransactions =
      FXCollections.observableArrayList();
<<<<<<< HEAD
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
=======

  private final StringProperty barcodeText = new SimpleStringProperty("--");
  private final StringProperty reorderingLevelText = new SimpleStringProperty("--");
  private final StringProperty taxCategoryText = new SimpleStringProperty("--");
  private final StringProperty weightText = new SimpleStringProperty("0");
  private final StringProperty unitTypeText = new SimpleStringProperty("--");
  private final StringProperty minStockLevelText = new SimpleStringProperty("--");
  private final StringProperty averageUnitCostText = new SimpleStringProperty("$0.00");
  private final StringProperty markupText = new SimpleStringProperty("0%");
  private final StringProperty totalValueText = new SimpleStringProperty("$0.00");
>>>>>>> origin/new

  public InventoryViewModel(
      ProductService productService,
      InventoryService inventoryService,
      SupplierService supplierService) {
    this.productService = productService;
    this.inventoryService = inventoryService;
    this.supplierService = supplierService;
<<<<<<< HEAD
=======
    this.setupSelectedItemListener();
    this.sortedList.setComparator(Comparator.comparing(Product::getName));
  }

  private void setupSelectedItemListener() {
>>>>>>> origin/new
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
<<<<<<< HEAD
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
=======
>>>>>>> origin/new
  }

  private void updatePresentation(Product product) {
    this.barcodeText.set(product.getBarcode());
    this.reorderingLevelText.set(String.valueOf(product.getReorderingLevel()));
    this.taxCategoryText.set(product.getTaxCategory());
    this.weightText.set(String.valueOf(product.getWeight()));
    this.unitTypeText.set(product.getUnitType());
    this.minStockLevelText.set(String.valueOf(product.getMinStockLevel()));
    this.averageUnitCostText.set(CurrencyUtil.format(product.getAverageUnitCost()));
<<<<<<< HEAD
    BigDecimal cost = product.getAverageUnitCost();
    BigDecimal price = product.getSellingPrice();
    if (cost == null || price == null || cost.compareTo(BigDecimal.ZERO) == 0) {
=======
    BigDecimal averageUnitCost =
        Optional.ofNullable(product.getAverageUnitCost()).orElse(BigDecimal.ZERO);
    BigDecimal price = product.getSellingPrice();
    if (price == null || averageUnitCost.signum() == 0) {
>>>>>>> origin/new
      this.markupText.set("0%");
    } else {
      BigDecimal markup =
          price
<<<<<<< HEAD
              .subtract(cost)
              .divide(cost, 4, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));
      this.markupText.set(NumberUtils.percentage(markup));
    }
    BigDecimal totalValue =
        product.getAverageUnitCost().multiply(BigDecimal.valueOf(product.getCurrentStock()));
    this.productTotalValueText.set(CurrencyUtil.format(totalValue));
=======
              .subtract(averageUnitCost)
              .divide(averageUnitCost, 4, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));
      this.markupText.set(NumberUtils.percentage(markup));
    }
    BigDecimal totalValue = averageUnitCost.multiply(BigDecimal.valueOf(product.getCurrentStock()));
    this.totalValueText.set(CurrencyUtil.format(totalValue));
  }

  private void refreshDetail(int productId) {
    this.executeLoadingTask(
        () -> {
          List<StockBatch> batches = this.inventoryService.listInventoryByProduct(productId);
          List<InventoryTransaction> transactions =
              this.inventoryService.getTransactionHistory(productId);
          return new Pair<>(batches, transactions);
        },
        result -> {
          if (this.selectedItem.get() != null && this.selectedItem.get().getId() == productId) {
            this.selectedProductBatches.setAll(result.getKey());
            this.selectedProductTransactions.setAll(result.getValue());
          }
        });
>>>>>>> origin/new
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
<<<<<<< HEAD
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
=======
    this.totalValueText.set("$0.00");
>>>>>>> origin/new
  }

  @Override
  protected List<Product> fetchItems() throws Exception {
<<<<<<< HEAD
    return productService.listProducts();
=======
    return this.productService.listProducts();
  }

  @Override
  protected boolean matchesSearch(Product product, String filterText) {
    return Stream.of(product.getName(), product.getSku(), product.getCategory())
        .filter(Objects::nonNull)
        .map(String::toLowerCase)
        .anyMatch(string -> string.contains(filterText));
  }

  @Override
  protected boolean isArchived(Product product) {
    return product.isDeleted();
>>>>>>> origin/new
  }

  @Override
  public void add(Product item) {
<<<<<<< HEAD
    this.runInventoryTask(() -> productService.createProduct(item));
=======
    this.runInventoryTask(() -> this.productService.createProduct(item));
>>>>>>> origin/new
  }

  @Override
  public void update(Product item) {
<<<<<<< HEAD
    this.runInventoryTask(() -> productService.updateProduct(item));
=======
    this.runInventoryTask(() -> this.productService.updateProduct(item));
  }

  @Override
  public void archive(Product item) {
    this.runInventoryTask(() -> this.productService.archive(item.getId()));
  }

  @Override
  public void restore(Product item) {
    this.runInventoryTask(() -> this.productService.restore(item.getId()));
>>>>>>> origin/new
  }

  @Override
  public void delete(Product item) {
<<<<<<< HEAD
    this.runInventoryTask(() -> productService.removeProduct(item.getId()));
  }

  public void receiveStock(StockReceiveRequest receiveRequest, int userId) {
    this.runInventoryTask(() -> inventoryService.receiveNewStock(receiveRequest, userId));
=======
    this.runInventoryTask(() -> this.productService.remove(item.getId()));
  }

  public void receiveStock(StockReceiveRequest receiveRequest, int userId) {
    this.runInventoryTask(() -> this.inventoryService.receiveNewStock(receiveRequest, userId));
>>>>>>> origin/new
  }

  private void runInventoryTask(TaskAction action) {
    this.runAsync(
        action,
        () -> {
<<<<<<< HEAD
          this.refresh();
=======
>>>>>>> origin/new
          if (this.selectedItem.get() != null) {
            this.refreshDetail(this.selectedItem.get().getId());
          }
        });
  }

<<<<<<< HEAD
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
=======
  public ProductService getProductService() {
    return this.productService;
  }

  public SupplierService getSupplierService() {
    return this.supplierService;
  }

  public final ObservableList<StockBatch> getSelectedProductBatches() {
    return this.selectedProductBatches;
  }

  public final ObservableList<InventoryTransaction> getSelectedProductTransactions() {
    return this.selectedProductTransactions;
  }

  public StringProperty barcodeTextProperty() {
    return this.barcodeText;
  }

  public StringProperty reorderingLevelTextProperty() {
    return this.reorderingLevelText;
  }

  public StringProperty taxCategoryTextProperty() {
    return this.taxCategoryText;
  }

  public StringProperty weightTextProperty() {
    return this.weightText;
  }

  public StringProperty unitTypeTextProperty() {
    return this.unitTypeText;
  }

  public StringProperty minStockLevelTextProperty() {
    return this.minStockLevelText;
  }

  public StringProperty averageUnitCostTextProperty() {
    return this.averageUnitCostText;
  }

  public StringProperty markupTextProperty() {
    return this.markupText;
  }

  public StringProperty totalValueTextProperty() {
    return this.totalValueText;
>>>>>>> origin/new
  }
}
