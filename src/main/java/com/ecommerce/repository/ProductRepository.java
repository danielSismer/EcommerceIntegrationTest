package com.ecommerce.repository;

import com.ecommerce.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    List<Product> findByPriceRange(Double minPrice, Double maxPrice);
    void update(Product product);
    void delete(Long id);
    boolean existsById(Long id);
}
