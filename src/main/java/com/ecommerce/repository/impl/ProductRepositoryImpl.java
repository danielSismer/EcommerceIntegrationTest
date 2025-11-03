package com.ecommerce.repository.impl;

import com.ecommerce.config.DatabaseConfig;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public Product save(Product product) {
        String sql = "INSERT INTO products (name, description, price, stock, category, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getStock());
            stmt.setString(5, product.getCategory());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getLong(1));
                }
            }
            
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving product", e);
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by id", e);
        }
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM products ORDER BY id";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all products", e);
        }
    }

    @Override
    public List<Product> findByCategory(String category) {
        String sql = "SELECT * FROM products WHERE category = ? ORDER BY name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
            
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by category", e);
        }
    }

    @Override
    public List<Product> findByPriceRange(Double minPrice, Double maxPrice) {
        String sql = "SELECT * FROM products WHERE price BETWEEN ? AND ? ORDER BY price";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, minPrice);
            stmt.setDouble(2, maxPrice);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
            
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by price range", e);
        }
    }

    @Override
    public void update(Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock = ?, " +
                     "category = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getStock());
            stmt.setString(5, product.getCategory());
            stmt.setLong(6, product.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM products WHERE id = ?";
        
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
            throw new RuntimeException("Error checking if product exists", e);
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStock(rs.getInt("stock"));
        product.setCategory(rs.getString("category"));
        product.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        product.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return product;
    }
}
