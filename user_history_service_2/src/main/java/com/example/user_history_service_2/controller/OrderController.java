package com.example.user_history_service_2.controller;

import com.example.user_history_service_2.model.Order;
import com.example.user_history_service_2.model.OrderDetail;
import com.example.user_history_service_2.service.OrderService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/customers")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{customerId}/orders")
    public ResponseEntity<List<Order>> getOrderHistory(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("day la ham lay lich su don hang cua nguoi dung");
        List<Order> orders = orderService.getOrderHistory(customerId, page, size);
        
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{customerId}/orders/{orderId}/details")
    public ResponseEntity<List<OrderDetail>> getOrderDetails(
            @PathVariable Long customerId,
            @PathVariable Long orderId) {

        log.info("day la ham lay chi tiet don hang");
        List<OrderDetail> details = orderService.getOrderDetails(customerId, orderId);
        return ResponseEntity.ok(details);
    }


}
