package com.ecommerce.service;

import com.ecommerce.model.Customer;
import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    Customer getCustomerById(Long id);
    Customer getCustomerByEmail(String email);
    List<Customer> getAllCustomers();
    Customer updateCustomer(Long id, Customer customer);
    void deleteCustomer(Long id);
    boolean customerExists(Long id);
}
