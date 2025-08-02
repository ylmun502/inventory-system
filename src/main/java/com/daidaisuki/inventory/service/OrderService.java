package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.*;
import com.daidaisuki.inventory.db.DatabaseManager;
import com.daidaisuki.inventory.model.*;

import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

public class OrderService {
    private final OrderDAO orderDAO;
    private final CustomerDAO customerDAO;
    private final OrderItemDAO orderItemDAO;
    private final ProductDAO prodctDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.customerDAO = new CustomerDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.prodctDAO = new ProductDAO();
    }

    public List<Order> getAllOrdersWithDetail() throws SQLException {
        List<Order> orders = orderDAO.getAllOrders();
        for(Order order : orders) {
            if(order.getCustomer() == null && order.getCustomerId() != 0) {
                Customer customer = customerDAO.getById(order.getCustomerId());
                order.setCustomer(customer);
            }
            List<OrderItem> items = orderItemDAO.getItemsByOrderId(order.getId());
            order.setItems(items);
        }
        return orders;
    }

    public void createOrderWithItems(Order order, List<OrderItem> items) throws SQLException {
        try(Connection conn = DatabaseManager.getConnection()) {
            try {
                conn.setAutoCommit(false);
                orderDAO.addOrder(order);
                for(OrderItem item : items) {
                    item.setOrderId(order.getId());
                    orderItemDAO.addOrderItem(item);
                    prodctDAO.decrementStock(item.getProductId(), item.getQuantity());
                }
                conn.commit();
            } catch(SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
