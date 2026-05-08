package org.bnb.trendyol_last.controller;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.bnb.trendyol_last.dto.CustomerDTO;
import org.bnb.trendyol_last.dto.CustomerRequestDTO;
import org.bnb.trendyol_last.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generatePdf() {
        try {
            List<CustomerDTO> customers = customerService.getAllCustomers();

            // Prevent Jasper errors if list is empty
            if (customers == null || customers.isEmpty()) {
                // Return a 204 No Content or a custom message
                return ResponseEntity.noContent().build();
            }

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(customers);

            // Dosya yolu "\src\main\resources\customer.jrxml" customer.jrxml diye dosya olmalı ki ona ulaşabilsin bir nevi blueprint pdfnin nasıl olacağını söyliyen
            org.springframework.core.io.ClassPathResource res = new org.springframework.core.io.ClassPathResource("customer.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(res.getInputStream());

            // Passing an empty HashMap for parameters instead of null is safer
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new java.util.HashMap<>(), dataSource);

            byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition", "inline; filename=customer_report.pdf")
                    .body(data);

        } catch (Exception e) {
            e.printStackTrace(); // Look at the terminal to see the REAL error
            return ResponseEntity.internalServerError().build();
        }
    }

}