package com.example.user_history_service_2.service;

import com.example.user_history_service_2.model.Customer;
import com.example.user_history_service_2.repository.CustomerRepository;
import com.example.user_history_service_2.response.CustomerResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final CustomerRepository customerRepository;

    public UserService(CustomerRepository customerRepository) {
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

    Customer findCustomerOrThrow(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Khach hang khong ton tai: " + customerId));
    }
}
