package com.ecommerce.service;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import java.util.List;

public interface OrderService {
    Order createOrder(Long customerId, List<OrderItem> items);
    Order getOrderById(Long id);
    List<Order> getAllOrders();
    List<Order> getOrdersByCustomerId(Long customerId);
    List<Order> getOrdersByStatus(String status);
    Order updateOrderStatus(Long orderId, String status);
    void cancelOrder(Long orderId);
    void deleteOrder(Long id);
}
