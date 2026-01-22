package com.daidaisuki.inventory.viewmodel.view;

import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.service.ProductService;
import com.daidaisuki.inventory.viewmodel.base.BaseListViewModel;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;

public class InventoryViewModel extends BaseListViewModel<Product> {
  private final ProductService productService;
  private final StringProperty searchFilter = new SimpleStringProperty();
  private final FilteredList<Product> filteredList;

  public InventoryViewModel(ProductService service) {
    this.productService = service;
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

  @Override
  protected List<Product> fetchItems() throws Exception {
    return productService.listProducts();
  }

  @Override
  public void add(Product item) throws Exception {
    productService.createProduct(item);
  }

  @Override
  public void update(Product item) throws Exception {
    productService.updateProduct(item);
  }

  @Override
  public void delete(Product item) throws Exception {
    productService.removeProduct(item.getId());
  }

  public final FilteredList<Product> getFilteredList() {
    return filteredList;
  }

  public final StringProperty searchFilterProperty() {
    return searchFilter;
  }
}
