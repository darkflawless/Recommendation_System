package com.example.recommend_service.controller;

import com.example.recommend_service.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/users/{userId}")
    public Map<String, Object> getRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "HOME_PAGE_BANNER") String location
    ) {
        return recommendationService.getRecommendations(userId, location);
    }
}
