package com.example.core.repository;

import com.example.core.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "orderDetails")
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId, Pageable pageable);
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId) ;
}
