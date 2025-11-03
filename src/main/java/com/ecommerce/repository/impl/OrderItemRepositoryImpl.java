package com.ecommerce.repository.impl;

import com.ecommerce.config.DatabaseConfig;
import com.ecommerce.model.OrderItem;
import com.ecommerce.repository.OrderItemRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderItemRepositoryImpl implements OrderItemRepository {

    @Override
    public OrderItem save(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, orderItem.getOrderId());
            stmt.setLong(2, orderItem.getProductId());
            stmt.setInt(3, orderItem.getQuantity());
            stmt.setBigDecimal(4, orderItem.getUnitPrice());
            stmt.setBigDecimal(5, orderItem.getSubtotal());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderItem.setId(generatedKeys.getLong(1));
                }
            }
            
            return orderItem;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order item", e);
        }
    }

    @Override
    public Optional<OrderItem> findById(Long id) {
        String sql = "SELECT * FROM order_items WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOrderItem(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order item by id", e);
        }
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToOrderItem(rs));
                }
            }
            
            return items;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order items by order id", e);
        }
    }

    @Override
    public void update(OrderItem orderItem) {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ?, " +
                     "unit_price = ?, subtotal = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, orderItem.getOrderId());
            stmt.setLong(2, orderItem.getProductId());
            stmt.setInt(3, orderItem.getQuantity());
            stmt.setBigDecimal(4, orderItem.getUnitPrice());
            stmt.setBigDecimal(5, orderItem.getSubtotal());
            stmt.setLong(6, orderItem.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order item", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM order_items WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order item", e);
        }
    }

    @Override
    public void deleteByOrderId(Long orderId) {
        String sql = "DELETE FROM order_items WHERE order_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order items by order id", e);
        }
    }

    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getLong("id"));
        item.setOrderId(rs.getLong("order_id"));
        item.setProductId(rs.getLong("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setSubtotal(rs.getBigDecimal("subtotal"));
        return item;
    }
}
