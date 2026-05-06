package com.example.statistic_service.listener;

import com.example.statistic_service.service.RecommendationProductStatsService;

import org.example.common.model.ClickEvent;
import org.example.common.model.ImpressionEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RecommendationStatsEventListener {

    private final RecommendationProductStatsService recommendationProductStatsService;

    public RecommendationStatsEventListener(RecommendationProductStatsService recommendationProductStatsService) {
        this.recommendationProductStatsService = recommendationProductStatsService;
    }

    @RabbitListener(queues = "${tracking.rabbit.queue.impression}")
    public void onImpression(ImpressionEvent event) {
        if (event == null || event.getDisplayedProductIds() == null || event.getDisplayedProductIds().isEmpty()) {
            return;
        }

        for (Long productId : event.getDisplayedProductIds()) {
            recommendationProductStatsService.recordImpression(productId, event.getCreatedAt());
        }
    }

    @RabbitListener(queues = "${tracking.rabbit.queue.click}")
    public void onClick(ClickEvent event) {
        if (event == null) {
            return;
        }
        recommendationProductStatsService.recordClick(event.getProductId(), event.getProductName(), event.getCreatedAt());
    }
}
