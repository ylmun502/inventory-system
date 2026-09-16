package com.daidaisuki.inventory.viewmodel.view;

import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.service.SupplierService;
import com.daidaisuki.inventory.viewmodel.base.BaseListViewModel;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SupplierViewModel extends BaseListViewModel<Supplier> {
  private SupplierService supplierService;

  public SupplierViewModel(SupplierService supplierService) {
    this.supplierService = supplierService;
  }

  @Override
  protected List<Supplier> fetchItems() throws Exception {
    return this.supplierService.listAll();
  }

  @Override
  protected boolean matchesSearch(Supplier supplier, String filterText) {
    return Stream.of(supplier.getName(), supplier.getShortCode())
        .filter(Objects::nonNull)
        .map(String::toLowerCase)
        .anyMatch(string -> string.contains(filterText));
  }

  @Override
  protected boolean isArchived(Supplier supplier) {
    return supplier.isDeleted();
  }

  @Override
  public void add(Supplier supplier) {
    this.runAsync(() -> this.supplierService.createSupplier(supplier), null);
  }

  @Override
  public void update(Supplier supplier) {
    this.runAsync(() -> this.supplierService.update(supplier), null);
  }

  @Override
  public void archive(Supplier supplier) {
    this.runAsync(() -> this.supplierService.archive(supplier.getId()), null);
  }

  @Override
  public void restore(Supplier supplier) {
    this.runAsync(() -> this.supplierService.restore(supplier.getId()), null);
  }

  @Override
  public void delete(Supplier supplier) {
    this.runAsync(() -> this.supplierService.remove(supplier.getId()), null);
  }

  public SupplierService getSupplierService() {
    return this.supplierService;
  }
}
