package com.ecommerce.repository;

import com.ecommerce.model.OrderItem;
import java.util.List;
import java.util.Optional;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);
    Optional<OrderItem> findById(Long id);
    List<OrderItem> findByOrderId(Long orderId);
    void update(OrderItem orderItem);
    void delete(Long id);
    void deleteByOrderId(Long orderId);
}
