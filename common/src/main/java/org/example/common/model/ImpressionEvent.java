package org.example.common.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;


@Data
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


}
