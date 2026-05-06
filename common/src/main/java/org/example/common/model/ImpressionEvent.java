package org.example.common.model;

import java.time.LocalDateTime;
import java.util.List;

public class ImpressionEvent {
    private String id;
    private Long userId;
    private String recommendationSessionId;
    private List<Long> displayedProductIds;
    private String location;
    private LocalDateTime createdAt;

    public ImpressionEvent() {
    }

    public ImpressionEvent(String id,
                           Long userId,
                           String recommendationSessionId,
                           List<Long> displayedProductIds,
                           String location,
                           LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.recommendationSessionId = recommendationSessionId;
        this.displayedProductIds = displayedProductIds;
        this.location = location;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRecommendationSessionId() {
        return recommendationSessionId;
    }

    public void setRecommendationSessionId(String recommendationSessionId) {
        this.recommendationSessionId = recommendationSessionId;
    }

    public List<Long> getDisplayedProductIds() {
        return displayedProductIds;
    }

    public void setDisplayedProductIds(List<Long> displayedProductIds) {
        this.displayedProductIds = displayedProductIds;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
