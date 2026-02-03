package com.daidaisuki.inventory.viewmodel.view;

import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.service.SupplierService;
import com.daidaisuki.inventory.viewmodel.base.BaseListViewModel;
import java.util.List;

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
  public void add(Supplier supplier) {
    runAsync(() -> this.supplierService.createSupplier(supplier), this::refresh);
  }

  @Override
  public void update(Supplier supplier) {
    runAsync(() -> this.supplierService.updateSupplier(supplier), this::refresh);
  }

  @Override
  public void delete(Supplier supplier) {
    runAsync(() -> this.supplierService.removeSupplier(supplier.getId()), this::refresh);
  }
}
