package com.example.user_history_service_2.repository;

import com.example.user_history_service_2.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("SELECT od FROM OrderDetail od JOIN FETCH od.product WHERE od.order.id = :orderId")
    List<OrderDetail> findByOrderId(@Param("orderId") Long orderId);
}
