package com.example.core.service;

import com.example.core.model.SearchHistory;
import com.example.core.repository.SearchHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserService userService;

    public SearchHistoryService(SearchHistoryRepository searchHistoryRepository,
                                UserService userService) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.userService = userService;
    }

    public List<SearchHistory> getSearchHistory(Long customerId, int page, int size) {
        userService.findCustomerOrThrow(customerId);
        Pageable pageable = PageRequest.of(page, size);
        return searchHistoryRepository.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable);
    }


}
