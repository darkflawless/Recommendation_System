
package com.example.core.customer ;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/search")

    public ResponseEntity<List<CustomerResponse>> searchCustomers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int size, 
            @RequestParam(defaultValue = "0") int page) {


        log.info("day la ham tim kiem nguoi dung");
        List<CustomerResponse> result = customerService.searchCustomers(keyword , page, size );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerInfo(@PathVariable Long customerId) {

        log.info("day la ham lay thong tin nguoi dung");
        CustomerResponse customer = customerService.getCustomerInfo(customerId);
        return ResponseEntity.ok(customer);
    }

}
