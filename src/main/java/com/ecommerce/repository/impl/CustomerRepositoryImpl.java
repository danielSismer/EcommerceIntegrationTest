package com.ecommerce.repository.impl;

import com.ecommerce.config.DatabaseConfig;
import com.ecommerce.model.Customer;
import com.ecommerce.repository.CustomerRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerRepositoryImpl implements CustomerRepository {

    @Override
    public Customer save(Customer customer) {
        String sql = "INSERT INTO customers (name, email, phone, address, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getLong(1));
                }
            }
            
            return customer;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving customer", e);
        }
    }

    @Override
    public Optional<Customer> findById(Long id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding customer by id", e);
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding customer by email", e);
        }
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT * FROM customers ORDER BY id";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
            
            return customers;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all customers", e);
        }
    }

    @Override
    public void update(Customer customer) {
        String sql = "UPDATE customers SET name = ?, email = ?, phone = ?, address = ?, " +
                     "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());
            stmt.setLong(5, customer.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM customers WHERE id = ?";
        
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
            throw new RuntimeException("Error checking if customer exists", e);
        }
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setName(rs.getString("name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        customer.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return customer;
    }
}
