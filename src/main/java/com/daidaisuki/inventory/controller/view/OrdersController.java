package com.daidaisuki.inventory.controller.view;

import java.time.LocalDate;

import com.daidaisuki.inventory.dao.OrderDAO;
import com.daidaisuki.inventory.model.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class OrdersController {
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> orderIdCol;
    @FXML private TableColumn<Order, String> customerNameCol;
    @FXML private TableColumn<Order, LocalDate> dateCol;
    @FXML private TableColumn<Order, Integer> totalItemsCol;
    @FXML private TableColumn<Order, Double> totalAmountCol;
    @FXML private TableColumn<Order, String> paymentMethodCol;

    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private final OrderDAO orderDao = new OrderDAO();
    private final ObservableList<Order> orderList = FXCollections.observableArrayList();

    @FXML
    private void handleAddProduct() {

    }

    @FXML
    private void handleEditProduct() {

    }

    @FXML
    private void handleDeleteProduct() {

    }
}
