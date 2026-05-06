package com.example.statistic_service.repository;

import com.example.statistic_service.model.RecommendationProductDailyStats;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecommendationProductDailyStatsRepository extends MongoRepository<RecommendationProductDailyStats, String> {

    Optional<RecommendationProductDailyStats> findByStatDateAndProductId(LocalDate statDate, Long productId);

    List<RecommendationProductDailyStats> findByStatDateBetween(LocalDate startDate, LocalDate endDate);

    List<RecommendationProductDailyStats> findByProductIdAndStatDateBetween(Long productId, LocalDate startDate, LocalDate endDate);

    List<RecommendationProductDailyStats> findByProductId(Long productId);
}
