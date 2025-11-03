package com.ecommerce.service.impl;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (product.getPrice() == null || product.getPrice().doubleValue() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice < 0 || maxPrice < 0) {
            throw new IllegalArgumentException("Prices cannot be negative");
        }
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        product.setId(id);
        productRepository.update(product);
        return productRepository.findById(id).orElseThrow();
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.delete(id);
    }

    @Override
    public boolean isProductAvailable(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        return product.getStock() >= quantity;
    }

    @Override
    public void updateStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }
        product.setStock(newStock);
        productRepository.update(product);
    }
}
