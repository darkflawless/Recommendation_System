package com.example.statistic_service.service;

import com.example.statistic_service.model.RecommendationProductDailyStats;
import com.example.statistic_service.model.RecommendationProductPeriodStats;
import com.example.statistic_service.model.SortType;
import com.example.statistic_service.repository.RecommendationProductDailyStatsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationProductStatsService {

    private final RecommendationProductDailyStatsRepository repository;
    private final MongoTemplate mongoTemplate;

    public RecommendationProductStatsService(
        RecommendationProductDailyStatsRepository repository,
        MongoTemplate mongoTemplate) {

        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public void recordImpression(Long productId, LocalDateTime createdAt) {
        if (productId == null) {
            return;
        }

        LocalDate statDate = normalizeDate(createdAt);
        
        Query query = Query.query(Criteria.where("statDate").is(statDate)
                .and("productId").is(productId));

        Update update = new Update()
                .setOnInsert("id", statDate + "_" + productId)
                .setOnInsert("statDate", statDate)
                .setOnInsert("productId", productId)
                .setOnInsert("productName", "Unknown")
                .inc("totalRecommendations", 1);

        
        mongoTemplate.upsert(query, update, RecommendationProductDailyStats.class);
    }

    public void recordClick(Long productId, String productName, LocalDateTime createdAt) {
        if (productId == null) {
            return;
        }

        LocalDate statDate = normalizeDate(createdAt);

        Query query = Query.query(Criteria.where("statDate").is(statDate)
                .and("productId").is(productId));

            
                Update update = new Update()
                .setOnInsert("id", statDate + "_" + productId)
                .setOnInsert("statDate", statDate)
                .setOnInsert("productId", productId)
                .setOnInsert("productName", "Unknown")
                .inc("totalViews", 1);
                
        RecommendationProductDailyStats existing = mongoTemplate.findOne(query, RecommendationProductDailyStats.class);

        if (productName != null && !productName.isBlank()) {
            update.set("productName", productName);
        }
        
        if (existing == null || existing.getTotalRecommendations() <= 0) {
            update.inc("totalRecommendations", 1);
        }

        if (existing == null) {
            mongoTemplate.upsert(query, update, RecommendationProductDailyStats.class);
        } else {
            mongoTemplate.updateFirst(query, update, RecommendationProductDailyStats.class);
        }
    }

    public Page<RecommendationProductPeriodStats> getPagedProducts(
        LocalDate startDate,
        LocalDate endDate,
        int page,
        int size,
        SortType sortType) {

        List<RecommendationProductPeriodStats> aggregated = aggregateByProduct(startDate, endDate);
        aggregated.sort(sortType.getComparator());

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), aggregated.size());
        List<RecommendationProductPeriodStats> content = fromIndex >= aggregated.size()
                ? List.of()
                : aggregated.subList(fromIndex, toIndex);

        return new PageImpl<>(content, pageable, aggregated.size());
    }


    public RecommendationProductPeriodStats getProductSummary(Long productId, LocalDate startDate, LocalDate endDate) {
        if (productId == null) {
            return null;
        }

        List<RecommendationProductDailyStats> all = repository.findByProductIdAndStatDateBetween(productId, startDate, endDate);
        RecommendationProductPeriodStats periodStats = new RecommendationProductPeriodStats(productId, "Unknown", 0, 0);
        for (RecommendationProductDailyStats daily : all) {
            periodStats.addDaily(daily);
        }
        return periodStats;
    }

    public RecommendationProductPeriodStats getProductSummaryAllTime(Long productId) {
        if (productId == null) {
            return null;
        }

        List<RecommendationProductDailyStats> all = repository.findByProductId(productId);
        RecommendationProductPeriodStats periodStats = new RecommendationProductPeriodStats(productId, "Unknown", 0, 0);
        for (RecommendationProductDailyStats daily : all) {
            periodStats.addDaily(daily);
        }
        return periodStats;
    }

    public List<RecommendationProductPeriodStats> getAllTimeProductList(int limit, SortType sortType) {
        List<RecommendationProductPeriodStats> aggregated = aggregateByProduct(repository.findAll());
        aggregated.sort(sortType.getComparator());

        if (limit <= 0 || limit >= aggregated.size()) {
            return aggregated;
        }
        return new ArrayList<>(aggregated.subList(0, limit));
    }

    public List<RecommendationProductDailyStats> getProductDailyStats(Long productId, LocalDate startDate, LocalDate endDate) {
        List<RecommendationProductDailyStats> daily = repository.findByProductIdAndStatDateBetween(productId, startDate, endDate);
        daily.sort(Comparator.comparing(RecommendationProductDailyStats::getStatDate));
        return daily;
    }

    private LocalDate normalizeDate(LocalDateTime createdAt) {
        return createdAt == null ? LocalDate.now() : createdAt.toLocalDate();
    }

    private List<RecommendationProductPeriodStats> aggregateByProduct(LocalDate startDate, LocalDate endDate) {
        List<RecommendationProductDailyStats> all = repository.findByStatDateBetween(startDate, endDate);
        return aggregateByProduct(all);
    }

    private List<RecommendationProductPeriodStats> aggregateByProduct(List<RecommendationProductDailyStats> all) {
        Map<Long, RecommendationProductPeriodStats> grouped = new HashMap<>();

        for (RecommendationProductDailyStats daily : all) {
            if (daily.getProductId() == null) {
                continue;
            }
            RecommendationProductPeriodStats period = grouped.computeIfAbsent(
                    daily.getProductId(),
                    id -> new RecommendationProductPeriodStats(id, "Unknown", 0, 0)
            );
            period.addDaily(daily);
        }

        return new ArrayList<>(grouped.values());
    }

    // private Comparator<RecommendationProductPeriodStats> buildComparator(String sortBy) {
    //     String normalized = sortBy == null ? "views" : sortBy.trim().toLowerCase();
    //     Comparator<RecommendationProductPeriodStats> comparator;

    //     if ("recommendations".equals(normalized)) {
    //         comparator = Comparator.comparingLong(RecommendationProductPeriodStats::getTotalRecommendations);
    //     } else if ("rate".equals(normalized)) {
    //         comparator = Comparator.comparingDouble(RecommendationProductPeriodStats::getViewRate);
    //     } else {
    //         comparator = Comparator.comparingLong(RecommendationProductPeriodStats::getTotalViews);
    //     }

    //     return comparator.reversed().thenComparing(RecommendationProductPeriodStats::getProductId);
    // }
    // không cần nữa, đã chuyển sang enum SortType
}
