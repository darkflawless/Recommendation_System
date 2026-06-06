package com.example.core.order;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.core.customer.CustomerService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CustomerService customerService;

    public OrderService(OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            CustomerService customerService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.customerService = customerService;
    }

    public List<Order> getOrderHistory(Long customerId, int page, int size) {
        customerService.findCustomerOrThrow(customerId);

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
        
        customerService.findCustomerOrThrow(customerId);

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

        customerService.findCustomerOrThrow(customerId);
        findOrderOfCustomerOrThrow(customerId, orderId);

        return orderDetailRepository.findByOrderId(orderId);

    }

    public List<Order> getMyOrders(Long customerId, int page, int size) {
        return getOrderHistory(customerId, page, size);
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
