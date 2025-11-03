package com.ecommerce.service.impl;

import com.ecommerce.model.Customer;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (customer.getEmail() == null || !customer.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + customer.getEmail());
        }
        
        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    @Override
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer updateCustomer(Long id, Customer customer) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customer.setId(id);
        customerRepository.update(customer);
        return customerRepository.findById(id).orElseThrow();
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.delete(id);
    }

    @Override
    public boolean customerExists(Long id) {
        return customerRepository.existsById(id);
    }
}
