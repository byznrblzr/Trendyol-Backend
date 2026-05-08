package org.bnb.trendyol_last.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private LocalDateTime orderDate;
    private String city;
    private String deliveryStatus;

    private Long customerId;
    private Long productId;
}
