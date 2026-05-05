package org.bnb.trendyol_last.service;

import org.bnb.trendyol_last.dto.OrderDTO;
import org.bnb.trendyol_last.dto.OrderRequestDTO;
import org.bnb.trendyol_last.exception.ErrorMessages;
import org.bnb.trendyol_last.exception.ResourceNotFoundException;
import org.bnb.trendyol_last.mapper.OrderMapper;
import org.bnb.trendyol_last.model.Customer;
import org.bnb.trendyol_last.model.Order;
import org.bnb.trendyol_last.model.Product;
import org.bnb.trendyol_last.repository.CustomerRepository;
import org.bnb.trendyol_last.repository.OrderRepository;
import org.bnb.trendyol_last.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;this.customerRepository = customerRepository;this.productRepository = productRepository;}

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(OrderMapper::toDTO).toList();
    }

    public OrderDTO getOrderById(long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_ORDER_NOT_FOUND + ": " + id));
        return OrderMapper.toDTO(order);
    }

    public OrderDTO createOrder(OrderRequestDTO requestOrder) {
        Customer customer = customerRepository.findById(requestOrder.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + ": " + requestOrder.getCustomerId()));

        Product product = productRepository.findById(requestOrder.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + requestOrder.getProductId()));

        Order order = OrderMapper.toEntity(requestOrder, customer, product);
        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toDTO(savedOrder);
    }

    public OrderDTO updateOrder(long id, OrderRequestDTO updateOrder) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_ORDER_NOT_FOUND + ": " + id));

        Customer customer = customerRepository.findById(updateOrder.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + ": " + updateOrder.getCustomerId()));

        Product product = productRepository.findById(updateOrder.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + updateOrder.getProductId()));

        OrderMapper.updateEntity(existingOrder, updateOrder, customer, product);
        Order updatedOrder = orderRepository.save(existingOrder);
        return OrderMapper.toDTO(updatedOrder);
    }

    public void deleteOrder(long id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_ORDER_NOT_FOUND + ": " + id));
        orderRepository.delete(existingOrder);
    }
}