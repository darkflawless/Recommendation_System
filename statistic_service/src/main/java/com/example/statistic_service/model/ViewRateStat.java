package com.example.statistic_service.model;

public interface ViewRateStat {

    long getTotalRecommendations();

    long getTotalViews();

    default double getViewRate() {
        if (getTotalRecommendations() <= 0) {
            return 0.0;
        }
        return (getTotalViews() * 100.0) / getTotalRecommendations();
    }
}
