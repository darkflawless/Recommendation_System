package com.example.statistic_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationProductPeriodStats implements ViewRateStat {

    private Long productId;

    private String productName;

    private long totalRecommendations;

    private long totalViews;

    public void addDaily(RecommendationProductDailyStats daily) {
        if (daily == null) {
            return;
        }
        this.totalRecommendations += daily.getTotalRecommendations();
        this.totalViews += daily.getTotalViews();
        if (daily.getProductName() != null && !daily.getProductName().isBlank()) {
            this.productName = daily.getProductName();
        }
    }
}
