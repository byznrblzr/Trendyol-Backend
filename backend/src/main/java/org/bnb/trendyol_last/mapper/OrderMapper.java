package org.bnb.trendyol_last.mapper;

import org.bnb.trendyol_last.dto.OrderDTO;
import org.bnb.trendyol_last.model.Customer;
import org.bnb.trendyol_last.model.Order;
import org.bnb.trendyol_last.model.Product;
import org.bnb.trendyol_last.dto.OrderRequestDTO;

public class OrderMapper {

    public static OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        return new OrderDTO(
                order.getId(),
                order.getOrderDate(),
                order.getCity(),
                order.getDeliveryStatus(),
                CustomerMapper.toDTO(order.getCustomer()),
                ProductMapper.toDTO(order.getProduct())
        );
    }

    public static Order toEntity(OrderRequestDTO requestDTO, Customer customer, Product product) {
        if (requestDTO == null) {
            return null;
        }

        Order order = new Order();
        order.setOrderDate(requestDTO.getOrderDate());
        order.setCity(requestDTO.getCity());
        order.setDeliveryStatus(requestDTO.getDeliveryStatus());
        order.setCustomer(customer);
        order.setProduct(product);

        return order;
    }

    public static void updateEntity(Order order, OrderRequestDTO requestDTO, Customer customer, Product product) {
        if (order == null || requestDTO == null) {
            return;
        }

        order.setOrderDate(requestDTO.getOrderDate());
        order.setCity(requestDTO.getCity());
        order.setDeliveryStatus(requestDTO.getDeliveryStatus());
        order.setCustomer(customer);
        order.setProduct(product);
    }
}