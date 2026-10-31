package com.daidaisuki.inventory.viewmodel.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BaseListViewModelTest {

  private TestListViewModel viewModel;

  @BeforeEach
  void setUp() {
    this.viewModel = new TestListViewModel();

    this.viewModel.setItems(
        List.of(
            new TestItem(1, "Alpha Product", false),
            new TestItem(2, "Beta Product", false),
            new TestItem(3, "Archived Alpha", true),
            new TestItem(4, "Archived Gamma", true)));
  }

  @Test
  void activeItemsShouldBeShownByDefault() {
    List<TestItem> visible = List.copyOf(this.viewModel.getSortedList());

    assertEquals(2, visible.size());

    assertTrue(visible.stream().allMatch(item -> !item.archived()));

    assertTrue(visible.stream().anyMatch(item -> item.name().equals("Alpha Product")));

    assertTrue(visible.stream().anyMatch(item -> item.name().equals("Beta Product")));
  }

  @Test
  void archivedItemsShouldBeHiddenByDefault() {
    List<TestItem> visible = List.copyOf(this.viewModel.getSortedList());

    assertFalse(visible.stream().anyMatch(TestItem::archived));
  }

  @Test
  void showArchivedShouldDisplayOnlyArchivedItems() {
    this.viewModel.showArchivedProperty().set(true);

    List<TestItem> visible = List.copyOf(this.viewModel.getSortedList());

    assertEquals(2, visible.size());

    assertTrue(visible.stream().allMatch(TestItem::archived));

    assertTrue(visible.stream().anyMatch(item -> item.name().equals("Archived Alpha")));

    assertTrue(visible.stream().anyMatch(item -> item.name().equals("Archived Gamma")));
  }

  @Test
  void searchFilterShouldFilterActiveItems() {
    this.viewModel.searchFilterProperty().set("alpha");

    List<TestItem> visible = List.copyOf(this.viewModel.getSortedList());

    assertEquals(1, visible.size());
    assertEquals("Alpha Product", visible.getFirst().name());
  }

  @Test
  void searchFilterShouldBeCaseInsensitive() {
    this.viewModel.searchFilterProperty().set("ALPHA");

    List<TestItem> visible = List.copyOf(this.viewModel.getSortedList());

    assertEquals(1, visible.size());
    assertEquals("Alpha Product", visible.getFirst().name());
  }

  @Test
  void searchFilterShouldTrimWhitespace() {
    this.viewModel.searchFilterProperty().set("   alpha   ");

    List<TestItem> visible = List.copyOf(this.viewModel.getSortedList());

    assertEquals(1, visible.size());
    assertEquals("Alpha Product", visible.getFirst().name());
  }

  @Test
  void archivedFilterAndSearchFilterShouldWorkTogether() {
    this.viewModel.showArchivedProperty().set(true);

    this.viewModel.searchFilterProperty().set("alpha");

    List<TestItem> visible = List.copyOf(this.viewModel.getSortedList());

    assertEquals(1, visible.size());
    assertEquals("Archived Alpha", visible.getFirst().name());

    assertTrue(visible.getFirst().archived());
  }

  @Test
  void searchShouldNotReturnArchivedMatchWhileShowingActiveItems() {
    this.viewModel.searchFilterProperty().set("gamma");

    assertTrue(this.viewModel.getSortedList().isEmpty());
  }

  @Test
  void blankSearchShouldShowAllItemsForCurrentArchiveMode() {
    this.viewModel.searchFilterProperty().set("alpha");

    assertEquals(1, this.viewModel.getSortedList().size());

    this.viewModel.searchFilterProperty().set("   ");

    assertEquals(2, this.viewModel.getSortedList().size());

    assertTrue(this.viewModel.getSortedList().stream().allMatch(item -> !item.archived()));
  }

  @Test
  void nullSearchShouldShowAllItemsForCurrentArchiveMode() {
    this.viewModel.searchFilterProperty().set(null);

    assertEquals(2, this.viewModel.getSortedList().size());

    assertTrue(this.viewModel.getSortedList().stream().allMatch(item -> !item.archived()));
  }

  @Test
  void clearingSearchShouldRestoreActiveItems() {
    this.viewModel.searchFilterProperty().set("alpha");

    assertEquals(1, this.viewModel.getSortedList().size());

    this.viewModel.searchFilterProperty().set("");

    assertEquals(2, this.viewModel.getSortedList().size());
  }

  private record TestItem(int id, String name, boolean archived) {}

  private static class TestListViewModel extends BaseListViewModel<TestItem> {

    void setItems(List<TestItem> items) {
      this.dataList.setAll(items);
    }

    @Override
    protected List<TestItem> fetchItems() {
      return List.of();
    }

    @Override
    protected boolean matchesSearch(TestItem item, String filterText) {

      return item.name().toLowerCase().contains(filterText);
    }

    @Override
    protected boolean isArchived(TestItem item) {
      return item.archived();
    }

    @Override
    public void add(TestItem item) {
      // Not relevant to filtering tests.
    }

    @Override
    public void update(TestItem item) {
      // Not relevant to filtering tests.
    }

    @Override
    public void archive(TestItem item) {
      // Not relevant to filtering tests.
    }

    @Override
    public void restore(TestItem item) {
      // Not relevant to filtering tests.
    }

    @Override
    public void delete(TestItem item) {
      // Not relevant to filtering tests.
    }
  }
}
