package com.example.core.repository;

import com.example.core.model.SearchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
}
