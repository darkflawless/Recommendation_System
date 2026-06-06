package com.example.core.searchHistory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.core.customer.CustomerService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final CustomerService customerService;

    public SearchHistoryService(SearchHistoryRepository searchHistoryRepository,
                                CustomerService customerService) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.customerService = customerService;
    }

    public List<SearchHistory> getSearchHistory(Long customerId, int page, int size) {
        
        customerService.findCustomerOrThrow(customerId);
        Pageable pageable = PageRequest.of(page, size);
        return searchHistoryRepository.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable);
    }


}
