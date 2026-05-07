
package com.example.core.controller;

import com.example.core.response.CustomerResponse;
import com.example.core.service.UserService;

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
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")

    public ResponseEntity<List<CustomerResponse>> searchCustomers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int size, 
            @RequestParam(defaultValue = "0") int page) {


        log.info("day la ham tim kiem nguoi dung");
        List<CustomerResponse> result = userService.searchCustomers(keyword , page, size );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerInfo(@PathVariable Long customerId) {

        log.info("day la ham lay thong tin nguoi dung");
        CustomerResponse customer = userService.getCustomerInfo(customerId);
        return ResponseEntity.ok(customer);
    }

}
