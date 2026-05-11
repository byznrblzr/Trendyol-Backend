package org.bnb.trendyol_last.controller;

import org.bnb.trendyol_last.dto.CustomerDTO;
import org.bnb.trendyol_last.dto.CustomerRequestDTO;
import org.bnb.trendyol_last.exception.ErrorMessages;
import org.bnb.trendyol_last.model.Customer;
import org.bnb.trendyol_last.service.CustomerService;
import org.bnb.trendyol_last.service.CustomerServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping(path = "/customer")
public class CustomerController {

    private final CustomerService customerService;
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable long id) {
        if(id == 0) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(customerService.getCustomerById(id), HttpStatus.OK) ;
    }

    @PostMapping(path = "/add")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerRequestDTO requestCustomer) {
        return new ResponseEntity<>(customerService.createCustomer(requestCustomer), HttpStatus.CREATED);
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable long id , @RequestBody CustomerRequestDTO updateCustomer) {
        return new ResponseEntity<>(customerService.updateCustomer(id  , updateCustomer), HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }


}