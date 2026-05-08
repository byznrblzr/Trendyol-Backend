package org.bnb.trendyol_last.service;

import org.bnb.trendyol_last.dto.OrderDTO;
import org.bnb.trendyol_last.dto.OrderRequestDTO;

import java.util.List;

public interface OrderService {
    public List<OrderDTO> getAllOrders();
    public OrderDTO getOrderById(long id);
    public OrderDTO createOrder(OrderRequestDTO requestOrder);
    public OrderDTO updateOrder(long id, OrderRequestDTO updateOrder);
    public void deleteOrder(long id);
}