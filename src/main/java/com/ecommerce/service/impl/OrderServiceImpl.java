package com.ecommerce.service.impl;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.Product;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.CustomerService;

import java.math.BigDecimal;
import java.util.List;

public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final CustomerService customerService;

    public OrderServiceImpl(OrderRepository orderRepository, 
                           OrderItemRepository orderItemRepository,
                           ProductService productService,
                           CustomerService customerService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productService = productService;
        this.customerService = customerService;
    }

    @Override
    public Order createOrder(Long customerId, List<OrderItem> items) {
        // Validate customer exists
        if (!customerService.customerExists(customerId)) {
            throw new RuntimeException("Customer not found with id: " + customerId);
        }
        
        // Validate items
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        // Calculate total and validate stock
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : items) {
            Product product = productService.getProductById(item.getProductId());
            
            // Check stock availability
            if (!productService.isProductAvailable(item.getProductId(), item.getQuantity())) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
            
            // Set unit price and calculate subtotal
            item.setUnitPrice(product.getPrice());
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setSubtotal(subtotal);
            totalAmount = totalAmount.add(subtotal);
        }
        
        // Create order
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order = orderRepository.save(order);
        
        // Save order items and update stock
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemRepository.save(item);
            productService.updateStock(item.getProductId(), -item.getQuantity());
        }
        
        order.setItems(items);
        return order;
    }

    @Override
    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        // Load order items
        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        order.setItems(items);
        
        return order;
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        // Load items for each order
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            order.setItems(items);
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        // Load items for each order
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            order.setItems(items);
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        // Load items for each order
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            order.setItems(items);
        }
        return orders;
    }

    @Override
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        
        // Validate status transition
        String currentStatus = order.getStatus();
        if ("CANCELLED".equals(currentStatus)) {
            throw new IllegalArgumentException("Cannot update a cancelled order");
        }
        if ("DELIVERED".equals(currentStatus)) {
            throw new IllegalArgumentException("Cannot update a delivered order");
        }
        
        order.setStatus(status);
        orderRepository.update(order);
        return getOrderById(orderId);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        
        if ("DELIVERED".equals(order.getStatus())) {
            throw new IllegalArgumentException("Cannot cancel a delivered order");
        }
        if ("CANCELLED".equals(order.getStatus())) {
            throw new IllegalArgumentException("Order is already cancelled");
        }
        
        // Restore stock
        for (OrderItem item : order.getItems()) {
            productService.updateStock(item.getProductId(), item.getQuantity());
        }
        
        order.setStatus("CANCELLED");
        orderRepository.update(order);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }
        
        // Delete order items first
        orderItemRepository.deleteByOrderId(id);
        
        // Delete order
        orderRepository.delete(id);
    }
}
