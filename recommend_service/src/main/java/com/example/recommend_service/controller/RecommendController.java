package com.example.recommend_service.controller;

import com.example.recommend_service.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> getRecommendations(
            @RequestParam Long userId) {
        log.info("Request gợi ý cho userId={}", userId);
        return ResponseEntity.ok(recommendService.getRecommendations(userId));
    }

    @GetMapping("/preference")
    public ResponseEntity<Map<String, Integer>> getPreference(@RequestParam Long userId) {
        return ResponseEntity.ok(recommendService.getUserPreference(userId));
    }
}
