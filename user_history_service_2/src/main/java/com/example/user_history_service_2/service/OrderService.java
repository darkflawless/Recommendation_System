package com.example.user_history_service_2.service;

import com.example.user_history_service_2.model.Order;
import com.example.user_history_service_2.model.OrderDetail;
import com.example.user_history_service_2.model.Product;
import com.example.user_history_service_2.repository.OrderDetailRepository;
import com.example.user_history_service_2.repository.OrderRepository;
import com.example.user_history_service_2.repository.ProductRepository;
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

        //calculate total amount for each order
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

    public List<OrderDetail> getOrderDetails(Long customerId, Long orderId) {
        userService.findCustomerOrThrow(customerId);

        findOrderOfCustomerOrThrow(customerId, orderId);

        return orderDetailRepository.findByOrderId(orderId);
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
