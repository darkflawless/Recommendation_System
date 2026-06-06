package com.example.core.searchHistory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
}
