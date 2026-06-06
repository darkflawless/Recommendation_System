package com.example.core.order;

import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.core.auth.UserPrincipal;


@Slf4j
@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasRole('USER')")
public class OrderCustomerController {

    private final OrderService orderService;

    public OrderCustomerController(OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public ResponseEntity<List<Order>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size ,
            @AuthenticationPrincipal UserPrincipal userPrincipal 
            ) {
        
        log.info("day la ham lay don hang cua customer");
   
        List<Order> orders = orderService.getMyOrders(userPrincipal.getId(), page , size);
        return ResponseEntity.ok(orders);
    }
 
}
