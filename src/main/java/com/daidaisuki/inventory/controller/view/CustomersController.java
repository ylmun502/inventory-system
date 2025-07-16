package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.dao.CustomerDAO;
import com.daidaisuki.inventory.model.Customer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CustomersController {
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> nameCol;
    @FXML private TableColumn<Customer, Integer> totalOrderCol;
    @FXML private TableColumn<Customer, Double> totalSpentCol;
    @FXML private TableColumn<Customer, String> platformCol;

    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private final CustomerDAO customerDao = new CustomerDAO();
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

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
