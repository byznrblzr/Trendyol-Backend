package org.bnb.trendyol_last.service;

import org.bnb.trendyol_last.dto.CustomerDTO;
import org.bnb.trendyol_last.dto.CustomerRequestDTO;
import org.bnb.trendyol_last.exception.ErrorMessages;
import org.bnb.trendyol_last.exception.ResourceAlreadyExistsException;
import org.bnb.trendyol_last.exception.ResourceNotFoundException;
import org.bnb.trendyol_last.mapper.CustomerMapper;
import org.bnb.trendyol_last.model.Customer;
import org.bnb.trendyol_last.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) { this.customerRepository = customerRepository;}


    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream().map(CustomerMapper::toDTO).toList();
    }

    public CustomerDTO getCustomerById(long id) {
       Customer customer = customerRepository.findById(id)
               .orElseThrow(()-> new ResourceNotFoundException(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + ": " + id));
       return CustomerMapper.toDTO(customer);
    }

    public CustomerDTO createCustomer(CustomerRequestDTO requestCustomer) {
        Customer customer = CustomerMapper.toEntity(requestCustomer);
        Customer savedcustomer = customerRepository.save(customer);
        return CustomerMapper.toDTO(savedcustomer);
    }

    public CustomerDTO updateCustomer(long id, CustomerRequestDTO updateCustomer) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + ": " + id
                ));
        CustomerMapper.updateEntity(existingCustomer, updateCustomer);
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return CustomerMapper.toDTO(updatedCustomer);
    }

    public void deleteCustomer(long id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + ": " + id));
        customerRepository.deleteById(id);
    }

}
