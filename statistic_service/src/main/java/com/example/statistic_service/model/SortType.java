
package com.example.statistic_service.model;

import java.util.Comparator;

public enum SortType {
    TOP_VIEW(Comparator.comparingLong(RecommendationProductPeriodStats::getTotalViews).reversed()),
    TOP_RECOMMENDATION(Comparator.comparingLong(RecommendationProductPeriodStats::getTotalRecommendations).reversed()), 
    TOP_VIEW_RATE(Comparator.comparingDouble(RecommendationProductPeriodStats::getViewRate).reversed());

    private final Comparator<RecommendationProductPeriodStats> comparator;

    SortType(Comparator<RecommendationProductPeriodStats> comparator) {
        this.comparator = comparator.thenComparing(RecommendationProductPeriodStats::getProductId);
    }   

    public Comparator<RecommendationProductPeriodStats> getComparator() {
        return this.comparator;
    }
}