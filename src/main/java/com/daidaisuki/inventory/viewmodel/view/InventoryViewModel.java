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
import java.util.Comparator;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
  private final SortedList<Product> sortedList;

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
    this.setupSelectedItemListener();
    this.sortedList.setComparator(Comparator.comparing(Product::getName));
  }

  private void setupSelectedItemListener() {
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
    this.totalValueText.set("$0.00");
  }

  @Override
  protected List<Product> fetchItems() throws Exception {
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
  }

  @Override
  public void add(Product item) {
    this.runInventoryTask(() -> this.productService.createProduct(item));
  }

  @Override
  public void update(Product item) {
    this.runInventoryTask(() -> this.productService.updateProduct(item));
  }

  @Override
  public void delete(Product item) {
    this.runInventoryTask(() -> this.productService.removeProduct(item.getId()));
  }

  public void receiveStock(StockReceiveRequest receiveRequest, int userId) {
    this.runInventoryTask(() -> this.inventoryService.receiveNewStock(receiveRequest, userId));
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

  public ProductService getProductService() {
    return productService;
  }

  public SupplierService getSupplierService() {
    return supplierService;
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

  public StringProperty productTotalValueTextProperty() {
    return this.productTotalValueText;
  }
}
