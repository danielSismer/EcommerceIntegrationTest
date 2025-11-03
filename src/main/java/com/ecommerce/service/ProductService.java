package com.ecommerce.service;

import com.ecommerce.model.Product;
import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product getProductById(Long id);
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    boolean isProductAvailable(Long productId, Integer quantity);
    void updateStock(Long productId, Integer quantity);
}
