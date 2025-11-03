package com.ecommerce;

import com.ecommerce.config.DatabaseConfig;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.impl.CustomerRepositoryImpl;
import com.ecommerce.repository.impl.OrderItemRepositoryImpl;
import com.ecommerce.repository.impl.OrderRepositoryImpl;
import com.ecommerce.repository.impl.ProductRepositoryImpl;
import com.ecommerce.service.CustomerService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.impl.CustomerServiceImpl;
import com.ecommerce.service.impl.OrderServiceImpl;
import com.ecommerce.service.impl.ProductServiceImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EcommerceIntegrationTest {
    
    private ProductRepository productRepository;
    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    
    private ProductService productService;
    private CustomerService customerService;
    private OrderService orderService;
    
    public static void main(String[] args) {
        EcommerceIntegrationTest test = new EcommerceIntegrationTest();
        test.setup();
        test.runAllTests();
    }
    
    private void setup() {
        System.out.println("=== SETTING UP DATABASE ===");
        
        productRepository = new ProductRepositoryImpl();
        customerRepository = new CustomerRepositoryImpl();
        orderRepository = new OrderRepositoryImpl();
        orderItemRepository = new OrderItemRepositoryImpl();
        
        // Initialize services
        productService = new ProductServiceImpl(productRepository);
        customerService = new CustomerServiceImpl(customerRepository);
        orderService = new OrderServiceImpl(orderRepository, orderItemRepository, productService, customerService);
        
        // Create tables
        executeSqlScript("scripts/create-tables.sql");
        
        // Seed data
        executeSqlScript("scripts/seed-data.sql");
        
        System.out.println("Database setup completed!\n");
    }
    
    private void executeSqlScript(String scriptPath) {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new FileReader(scriptPath))) {
            
            StringBuilder sql = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                sql.append(line).append(" ");
                if (line.endsWith(";")) {
                    stmt.execute(sql.toString());
                    sql.setLength(0);
                }
            }
            
            System.out.println("Executed script: " + scriptPath);
        } catch (Exception e) {
            System.err.println("Error executing script: " + scriptPath);
            e.printStackTrace();
        }
    }
    
    private void runAllTests() {
        System.out.println("\n=== STARTING INTEGRATION TESTS ===\n");
        
        testProductOperations();
        testCustomerOperations();
        testOrderCreation();
        testOrderStatusUpdate();
        testOrderCancellation();
        testStockManagement();
        testComplexQueries();
        testErrorHandling();
        
        System.out.println("\n=== ALL TESTS COMPLETED ===");
    }
    
    private void testProductOperations() {
        System.out.println("--- Test 1: Product Operations ---");
        
        // Create new product
        Product newProduct = new Product();
        newProduct.setName("MacBook Pro 16");
        newProduct.setDescription("Powerful laptop for professionals");
        newProduct.setPrice(new BigDecimal("2499.99"));
        newProduct.setStock(10);
        newProduct.setCategory("Electronics");
        
        Product savedProduct = productService.createProduct(newProduct);
        System.out.println("Created product: " + savedProduct);
        
        // Find by ID
        Product foundProduct = productService.getProductById(savedProduct.getId());
        System.out.println("Found product by ID: " + foundProduct);
        
        // Find by category
        List<Product> electronics = productService.getProductsByCategory("Electronics");
        System.out.println("Electronics products count: " + electronics.size());
        
        // Find by price range
        List<Product> affordableProducts = productService.getProductsByPriceRange(50.0, 200.0);
        System.out.println("Products in $50-$200 range: " + affordableProducts.size());
        
        // Update product
        foundProduct.setPrice(new BigDecimal("2399.99"));
        productService.updateProduct(foundProduct.getId(), foundProduct);
        System.out.println("Updated product price");
        
        System.out.println("✓ Product operations test passed\n");
    }
    
    private void testCustomerOperations() {
        System.out.println("--- Test 2: Customer Operations ---");
        
        // Create new customer
        Customer newCustomer = new Customer();
        newCustomer.setName("Maria Garcia");
        newCustomer.setEmail("maria.garcia@email.com");
        newCustomer.setPhone("+1-555-0106");
        newCustomer.setAddress("999 Broadway, Miami, FL 33101");
        
        Customer savedCustomer = customerService.createCustomer(newCustomer);
        System.out.println("Created customer: " + savedCustomer);
        
        // Find by email
        Customer foundByEmail = customerService.getCustomerByEmail("maria.garcia@email.com");
        System.out.println("Found customer by email: " + foundByEmail);
        
        // Get all customers
        List<Customer> allCustomers = customerService.getAllCustomers();
        System.out.println("Total customers: " + allCustomers.size());
        
        // Update customer
        foundByEmail.setPhone("+1-555-9999");
        customerService.updateCustomer(foundByEmail.getId(), foundByEmail);
        System.out.println("Updated customer phone");
        
        System.out.println("✓ Customer operations test passed\n");
    }
    
    private void testOrderCreation() {
        System.out.println("--- Test 3: Order Creation ---");
        
        // Get customer
        Customer customer = customerService.getCustomerByEmail("john.doe@email.com");
        
        // Get products
        Product laptop = productService.getProductById(1L);
        Product headphones = productService.getProductById(6L);
        
        // Create order items
        List<OrderItem> items = new ArrayList<>();
        
        OrderItem item1 = new OrderItem();
        item1.setProductId(laptop.getId());
        item1.setQuantity(1);
        items.add(item1);
        
        OrderItem item2 = new OrderItem();
        item2.setProductId(headphones.getId());
        item2.setQuantity(2);
        items.add(item2);
        
        // Create order
        Order order = orderService.createOrder(customer.getId(), items);
        System.out.println("Created order: " + order);
        System.out.println("Order total: $" + order.getTotalAmount());
        System.out.println("Order items: " + order.getItems().size());
        
        // Verify stock was reduced
        Product updatedLaptop = productService.getProductById(laptop.getId());
        System.out.println("Laptop stock after order: " + updatedLaptop.getStock());
        
        System.out.println("✓ Order creation test passed\n");
    }
    
    private void testOrderStatusUpdate() {
        System.out.println("--- Test 4: Order Status Update ---");
        
        // Get an order
        List<Order> orders = orderService.getAllOrders();
        if (!orders.isEmpty()) {
            Order order = orders.get(0);
            System.out.println("Current order status: " + order.getStatus());
            
            // Update status
            order = orderService.updateOrderStatus(order.getId(), "PROCESSING");
            System.out.println("Updated order status: " + order.getStatus());
            
            order = orderService.updateOrderStatus(order.getId(), "SHIPPED");
            System.out.println("Updated order status: " + order.getStatus());
            
            order = orderService.updateOrderStatus(order.getId(), "DELIVERED");
            System.out.println("Updated order status: " + order.getStatus());
        }
        
        System.out.println("✓ Order status update test passed\n");
    }
    
    private void testOrderCancellation() {
        System.out.println("--- Test 5: Order Cancellation ---");
        
        // Create a new order to cancel
        Customer customer = customerService.getCustomerByEmail("jane.smith@email.com");
        Product product = productService.getProductById(4L); // Nike shoes
        
        int stockBefore = product.getStock();
        System.out.println("Stock before order: " + stockBefore);
        
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProductId(product.getId());
        item.setQuantity(2);
        items.add(item);
        
        Order order = orderService.createOrder(customer.getId(), items);
        System.out.println("Created order ID: " + order.getId());
        
        Product productAfterOrder = productService.getProductById(product.getId());
        System.out.println("Stock after order: " + productAfterOrder.getStock());
        
        // Cancel order
        orderService.cancelOrder(order.getId());
        System.out.println("Order cancelled");
        
        Product productAfterCancel = productService.getProductById(product.getId());
        System.out.println("Stock after cancellation: " + productAfterCancel.getStock());
        
        System.out.println("✓ Order cancellation test passed\n");
    }
    
    private void testStockManagement() {
        System.out.println("--- Test 6: Stock Management ---");
        
        Product product = productService.getProductById(7L); // Kindle
        System.out.println("Initial stock: " + product.getStock());
        
        // Check availability
        boolean available = productService.isProductAvailable(product.getId(), 5);
        System.out.println("Is 5 units available? " + available);
        
        // Update stock (add inventory)
        productService.updateStock(product.getId(), 10);
        Product updated = productService.getProductById(product.getId());
        System.out.println("Stock after adding 10 units: " + updated.getStock());
        
        // Update stock (remove inventory)
        productService.updateStock(product.getId(), -5);
        updated = productService.getProductById(product.getId());
        System.out.println("Stock after removing 5 units: " + updated.getStock());
        
        System.out.println("✓ Stock management test passed\n");
    }
    
    private void testComplexQueries() {
        System.out.println("--- Test 7: Complex Queries ---");
        
        // Get orders by customer
        Customer customer = customerService.getCustomerByEmail("john.doe@email.com");
        List<Order> customerOrders = orderService.getOrdersByCustomerId(customer.getId());
        System.out.println("Orders for " + customer.getName() + ": " + customerOrders.size());
        
        // Get orders by status
        List<Order> pendingOrders = orderService.getOrdersByStatus("PENDING");
        System.out.println("Pending orders: " + pendingOrders.size());
        
        List<Order> deliveredOrders = orderService.getOrdersByStatus("DELIVERED");
        System.out.println("Delivered orders: " + deliveredOrders.size());
        
        // Get products by category
        List<Product> clothing = productService.getProductsByCategory("Clothing");
        System.out.println("Clothing products: " + clothing.size());
        
        List<Product> footwear = productService.getProductsByCategory("Footwear");
        System.out.println("Footwear products: " + footwear.size());
        
        System.out.println("✓ Complex queries test passed\n");
    }
    
    private void testErrorHandling() {
        System.out.println("--- Test 8: Error Handling ---");
        
        // Test 1: Try to create product with invalid data
        try {
            Product invalidProduct = new Product();
            invalidProduct.setName("");
            invalidProduct.setPrice(new BigDecimal("-10"));
            productService.createProduct(invalidProduct);
            System.out.println("✗ Should have thrown exception for invalid product");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly caught invalid product: " + e.getMessage());
        }
        
        // Test 2: Try to create customer with duplicate email
        try {
            Customer duplicate = new Customer();
            duplicate.setName("Duplicate User");
            duplicate.setEmail("john.doe@email.com"); // Already exists
            duplicate.setPhone("+1-555-0000");
            customerService.createCustomer(duplicate);
            System.out.println("✗ Should have thrown exception for duplicate email");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly caught duplicate email: " + e.getMessage());
        }
        
        // Test 3: Try to create order with insufficient stock
        try {
            Customer customer = customerService.getCustomerByEmail("bob.johnson@email.com");
            List<OrderItem> items = new ArrayList<>();
            OrderItem item = new OrderItem();
            item.setProductId(1L);
            item.setQuantity(1000); // More than available
            items.add(item);
            orderService.createOrder(customer.getId(), items);
            System.out.println("✗ Should have thrown exception for insufficient stock");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly caught insufficient stock: " + e.getMessage());
        }
        
        // Test 4: Try to find non-existent product
        try {
            productService.getProductById(99999L);
            System.out.println("✗ Should have thrown exception for non-existent product");
        } catch (RuntimeException e) {
            System.out.println("✓ Correctly caught non-existent product: " + e.getMessage());
        }
        
        // Test 5: Try to cancel delivered order
        try {
            List<Order> delivered = orderService.getOrdersByStatus("DELIVERED");
            if (!delivered.isEmpty()) {
                orderService.cancelOrder(delivered.get(0).getId());
                System.out.println("✗ Should have thrown exception for cancelling delivered order");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly caught delivered order cancellation: " + e.getMessage());
        }
        
        System.out.println("✓ Error handling test passed\n");
    }
}
