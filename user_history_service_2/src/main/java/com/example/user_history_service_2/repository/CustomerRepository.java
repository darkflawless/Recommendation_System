

package com.example.user_history_service_2.repository;

import com.example.user_history_service_2.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

Page<Customer> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
    String usernameKeyword,
    String emailKeyword,
    Pageable pageable
);
}
