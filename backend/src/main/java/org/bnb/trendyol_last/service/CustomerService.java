package org.bnb.trendyol_last.service;

import org.bnb.trendyol_last.dto.CustomerDTO;
import org.bnb.trendyol_last.dto.CustomerRequestDTO;
import org.bnb.trendyol_last.model.Customer;

import java.util.List;

public interface CustomerService {
    public List<CustomerDTO> getAllCustomers();
    public CustomerDTO getCustomerById(long id);
    public CustomerDTO createCustomer(CustomerRequestDTO requestCustomer);
    public CustomerDTO updateCustomer(long id, CustomerRequestDTO updateCustomer);
    public void deleteCustomer(long id);
}
