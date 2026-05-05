package org.bnb.trendyol_last.mapper;

import org.bnb.trendyol_last.dto.CustomerDTO;
import org.bnb.trendyol_last.dto.CustomerRequestDTO;
import org.bnb.trendyol_last.model.Customer;

public class CustomerMapper {

    public static CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getPhone()
        );
    }

    public static Customer toEntity(CustomerRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setName(requestDTO.getName());
        customer.setAddress(requestDTO.getAddress());
        customer.setPhone(requestDTO.getPhone());

        return customer;
    }

    public static void updateEntity(Customer customer, CustomerRequestDTO requestDTO) {
        if (customer == null || requestDTO == null) {
            return;
        }

        customer.setName(requestDTO.getName());
        customer.setAddress(requestDTO.getAddress());
        customer.setPhone(requestDTO.getPhone());
    }
}