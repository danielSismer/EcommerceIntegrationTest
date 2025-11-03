package com.ecommerce.repository;

import com.ecommerce.model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByStatus(String status);
    void update(Order order);
    void delete(Long id);
    boolean existsById(Long id);
}
