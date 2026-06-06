
package com.example.core.customer;


import java.util.List;

import org.springframework.stereotype.Service;

import lombok.NonNull;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
        public List<CustomerResponse> searchCustomers(String keyword, int page , int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customers = customerRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);

        return customers.stream().map(CustomerResponse::fromEntity).toList();
    }


    public CustomerResponse getCustomerInfo(Long customerId) {
        Customer customer = findCustomerOrThrow(customerId);
        return CustomerResponse.fromEntity(customer);
    }

    public Customer findCustomerOrThrow(@NonNull Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Khach hang khong ton tai: " + customerId));
    }

}