package com.example.user_history_service_2.controller;

import com.example.user_history_service_2.model.SearchHistory;
import com.example.user_history_service_2.service.SearchHistoryService;

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
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    public SearchHistoryController(SearchHistoryService searchHistoryService) {
        this.searchHistoryService = searchHistoryService;
    }

    @GetMapping("/{customerId}/search-history")
    public ResponseEntity<List<SearchHistory>> getSearchHistory(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("day la ham lay lich su tim kiem cua nguoi dung");
        List<SearchHistory> history = searchHistoryService.getSearchHistory(customerId, page, size);
        return ResponseEntity.ok(history);
    }


}
