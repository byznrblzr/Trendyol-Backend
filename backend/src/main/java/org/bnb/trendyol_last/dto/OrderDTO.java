package org.bnb.trendyol_last.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private long id;
    private LocalDateTime orderDate;
    private String city;
    private String deliveryStatus;

    private CustomerDTO customerDTO;
    private ProductDTO productDTO;


}
