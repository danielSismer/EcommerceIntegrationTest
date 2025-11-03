package com.ecommerce.repository.impl;

import com.ecommerce.config.DatabaseConfig;
import com.ecommerce.model.Order;
import com.ecommerce.repository.OrderRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public Order save(Order order) {
        String sql = "INSERT INTO orders (customer_id, total_amount, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, order.getCustomerId());
            stmt.setBigDecimal(2, order.getTotalAmount());
            stmt.setString(3, order.getStatus());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getLong(1));
                }
            }
            
            return order;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order", e);
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOrder(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order by id", e);
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders ORDER BY id";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
            
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all orders", e);
        }
    }

    @Override
    public List<Order> findByCustomerId(Long customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
            
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding orders by customer id", e);
        }
    }

    @Override
    public List<Order> findByStatus(String status) {
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
            
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding orders by status", e);
        }
    }

    @Override
    public void update(Order order) {
        String sql = "UPDATE orders SET customer_id = ?, total_amount = ?, status = ?, " +
                     "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, order.getCustomerId());
            stmt.setBigDecimal(2, order.getTotalAmount());
            stmt.setString(3, order.getStatus());
            stmt.setLong(4, order.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM orders WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if order exists", e);
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setCustomerId(rs.getLong("customer_id"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        order.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return order;
    }
}
