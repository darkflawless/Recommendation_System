package com.example.recommend_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.example.common.model.ImpressionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class RecommendationService {

    private final RestClient restClient;
    private final String productBaseUrl;
    private final String trackingImpressionUrl;
    private final List<Long> candidateProductIds;
    private final int displayCount;

    public RecommendationService(
            RestClient restClient,
            @Value("${recommend.product.base-url}") String productBaseUrl,
            @Value("${recommend.tracking.impression-url}") String trackingImpressionUrl,
            @Value("${recommend.seed.candidate-product-ids}") List<Long> candidateProductIds,
            @Value("${recommend.seed.display-count:3}") int displayCount
    ) {
        this.restClient = restClient;
        this.productBaseUrl = productBaseUrl;
        this.trackingImpressionUrl = trackingImpressionUrl;
        this.candidateProductIds = candidateProductIds;
        this.displayCount = displayCount;
    }

        public Map<String, Object> getRecommendations(Long userId, String location) {
        List<Long> selectedIds = pickRandomProductIds();
        List<JsonNode> products = hydrateProducts(selectedIds);

        List<Long> hydratedIds = products.stream()
            .map(product -> product.path("id").asLong())
                .toList();

        String sessionId = UUID.randomUUID().toString();
        publishImpressionEvent(userId, sessionId, hydratedIds, location);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", userId);
        response.put("recommendationSessionId", sessionId);
        response.put("location", location);
        response.put("displayedProductIds", hydratedIds);
        response.put("products", products);
        return response;
    }

    private List<Long> pickRandomProductIds() {
        List<Long> shuffled = new ArrayList<>(candidateProductIds);
        Collections.shuffle(shuffled);
        int limit = Math.min(displayCount, shuffled.size());
        return shuffled.subList(0, limit);
    }

    private List<JsonNode> hydrateProducts(List<Long> productIds) {
        List<JsonNode> products = new ArrayList<>();
        for (Long productId : productIds) {
            try {
                JsonNode product = restClient.get()
                        .uri(productBaseUrl + "/api/products/{id}", productId)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(JsonNode.class);
                if (product != null && product.has("id")) {
                    products.add(product);
                }
            } catch (RestClientException ex) {
                log.warn("Skip product {} because hydration failed: {}", productId, ex.getMessage());
            }
        }
        return products;
    }

    private void publishImpressionEvent(Long userId, String sessionId, List<Long> productIds, String location) {
        ImpressionEvent event = new ImpressionEvent();
        event.setId(UUID.randomUUID().toString());
        event.setUserId(userId);
        event.setRecommendationSessionId(sessionId);
        event.setDisplayedProductIds(productIds);
        event.setLocation(location);
        event.setCreatedAt(LocalDateTime.now());

        try {
            restClient.post()
                    .uri(trackingImpressionUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(event)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.warn("Failed to publish impression event for session {}: {}", sessionId, ex.getMessage());
        }
    }
}
