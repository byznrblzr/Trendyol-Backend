package org.bnb.trendyol_last.controller;

import org.bnb.trendyol_last.dto.OrderDTO;
import org.bnb.trendyol_last.dto.OrderRequestDTO;
import org.bnb.trendyol_last.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {this.orderService = orderService;}

    @GetMapping(path = "/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable long id) {
        return new ResponseEntity<>(orderService.getOrderById(id), HttpStatus.OK);
    }

    @PostMapping(path = "/add")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderRequestDTO requestOrder) {
        return new ResponseEntity<>(orderService.createOrder(requestOrder), HttpStatus.CREATED);
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable long id, @RequestBody OrderRequestDTO updateOrder) {
        return new ResponseEntity<>(orderService.updateOrder(id, updateOrder), HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}