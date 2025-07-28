package com.daidaisuki.inventory.controller.view;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import com.daidaisuki.inventory.App;
import com.daidaisuki.inventory.base.controller.BaseTableController;
import com.daidaisuki.inventory.controller.dialog.OrderDialogController;
import com.daidaisuki.inventory.dao.OrderDAO;
import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.model.Order;
import com.daidaisuki.inventory.util.FxWindowUtils;
import com.daidaisuki.inventory.util.TableColumnUtils;
import com.daidaisuki.inventory.util.ViewLoader;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class OrdersController extends BaseTableController<Order> {
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Integer> orderIdCol;
    @FXML private TableColumn<Order, String> customerNameCol;
    @FXML private TableColumn<Order, LocalDate> dateCol;
    @FXML private TableColumn<Order, Integer> totalItemsCol;
    @FXML private TableColumn<Order, Double> totalAmountCol;
    @FXML private TableColumn<Order, Double> discountAmount;
    @FXML private TableColumn<Order, String> paymentMethodCol;

    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private final OrderDAO orderDAO = new OrderDAO();

    public void initialize() {
        setupColumns();
        initializeBase(orderTable, addButton, editButton, deleteButton);
        orderIdCol.setSortType(TableColumn.SortType.ASCENDING);
        orderTable.getSortOrder().add(orderIdCol);
    }

    public void setupColumns() {
        orderIdCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        List<Double> ratios = new ArrayList<>(Arrays.asList(0.06, 0.18, 0.1, 0.13, 0.16, 0.19, 0.18));
        TableColumnUtils.bindColumnWidthsByRatio(orderTable, ratios);
        orderIdCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        customerNameCol.setCellValueFactory(cellData -> cellData.getValue().getCustomer().nameProperty());
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        //totalItemsCol.setCellValueFactory(cellData -> );
        //totalAmountCol.setCellValueFactory(cellData -> );
    }

    @Override
    protected List<Order> fetchFromDB() throws SQLException {
        return orderDAO.getAllOrders();
    }

    @Override
    protected Window getWindow() {
        return FxWindowUtils.getWindow(orderTable);
    }

    @Override
    protected void addItem(Order order) throws SQLException {
        orderDAO.addOrder(order);
    }

    @Override
    protected void updateItem(Order order) throws SQLException {
        orderDAO.updateOrder(order);
    }

    @Override
    protected void deleteItem(Order order) throws SQLException {
        orderDAO.deleteOrder(order.getId());
    }

    @Override
    protected Order showDialog(Order orderToEdit) {
        return showOrderDialog(orderToEdit);
    }

    private Order showOrderDialog(Order orderToEdit) {
        try {
            FXMLLoader loader = ViewLoader.loadFxml(DialogView.ORDER_DIALOG);
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(orderTable.getScene().getWindow());
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
            dialogStage.setScene(scene);

            OrderDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setModel(orderToEdit);

            dialogStage.showAndWait();

            return controller.isSaveClicked() ? controller.getModel() : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
