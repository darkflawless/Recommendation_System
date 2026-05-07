package com.example.core.service;

import com.example.core.model.Order;
import com.example.core.model.OrderDetail;
import com.example.core.model.Product;
import com.example.core.repository.OrderDetailRepository;
import com.example.core.repository.OrderRepository;
import com.example.core.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            ProductRepository productRepository,
            UserService userService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public List<Order> getOrderHistory(Long customerId, int page, int size) {
        userService.findCustomerOrThrow(customerId);

        Pageable pageable = PageRequest.of(page, size);

        // calculate total amount for each order
        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId, pageable);
        for (Order order : orders) {
            double total = 0.0;
            for (OrderDetail detail : order.getOrderDetails()) {
                if (detail.getUnitPrice() == null || detail.getQuantity() == null) {
                    continue;
                }
                total += detail.getUnitPrice() * detail.getQuantity();
            }
            order.setTotalAmount(total);
        }
        return orders;
    }

    public List<Order> getOrderHistory(Long customerId) {
        userService.findCustomerOrThrow(customerId);
        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
        for (Order order : orders) {
            double total = 0.0;
            for (OrderDetail detail : order.getOrderDetails()) {
                if (detail.getUnitPrice() == null || detail.getQuantity() == null) {
                    continue;
                }
                total += detail.getUnitPrice() * detail.getQuantity();
            }
            order.setTotalAmount(total);
        }
        return orders;
    }

    public List<OrderDetail> getOrderDetails(Long customerId, Long orderId) {
        userService.findCustomerOrThrow(customerId);

        List<Order> orders = getOrderHistory(customerId);
        if (orders.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Long nextOrderId = getNextOrderIdOrThrow(orders, orderId);
        findOrderOfCustomerOrThrow(customerId, nextOrderId);

        return orderDetailRepository.findByOrderId(nextOrderId);
    }

    private Long getNextOrderIdOrThrow(List<Order> orders, Long currentOrderId) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId().equals(currentOrderId)) {
                int nextIndex = (i + 1) % orders.size();
                return orders.get(nextIndex).getId();
            }
        }

        throw new IllegalArgumentException("Don hang khong ton tai trong danh sach: " + currentOrderId);
    }


    private Order findOrderOfCustomerOrThrow(Long customerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Don hang khong ton tai: " + orderId));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Don hang " + orderId + " khong thuoc ve khach hang " + customerId);
        }

        return order;
    }
}
