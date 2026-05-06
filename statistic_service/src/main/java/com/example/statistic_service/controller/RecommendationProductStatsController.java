package com.example.statistic_service.controller;

import com.example.statistic_service.model.RecommendationProductDailyStats;
import com.example.statistic_service.model.RecommendationProductPeriodStats;
import com.example.statistic_service.model.SortType;
import com.example.statistic_service.service.RecommendationProductStatsService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping({
    "/statistics/recommendation-products",
    "/api/admin/statistics/recommendation-products",
})
public class RecommendationProductStatsController {

    private final RecommendationProductStatsService recommendationProductStatsService;

    public RecommendationProductStatsController(RecommendationProductStatsService recommendationProductStatsService) {
        this.recommendationProductStatsService = recommendationProductStatsService;
    }
    @GetMapping
    public ResponseEntity<Page<RecommendationProductPeriodStats>> getPagedProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size,
            @RequestParam(defaultValue = "TOP_VIEW") SortType sortType) {
        if (isInvalidRange(startDate, endDate)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(recommendationProductStatsService.getPagedProducts(startDate, endDate, page, size, sortType));
    }

    @GetMapping("/products")
    public ResponseEntity<List<RecommendationProductPeriodStats>> getAllTimeProducts(
            @RequestParam(defaultValue = "3") @Min(1) int limit,
            @RequestParam(defaultValue = "TOP_VIEW") SortType sortType) {

        log.info("day la ham lay danh sach san pham duoc de xuat nhieu nhat");
        int safeLimit = Math.min(limit, 20);
        return ResponseEntity.ok(recommendationProductStatsService.getAllTimeProductList(safeLimit, sortType));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<RecommendationProductPeriodStats> getProductSummary(
            @PathVariable Long productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("day la ham lay thong ke chi tiet san pham duoc de xuat nhieu nhat trong khoang thoi gian");
        if (isInvalidRange(startDate, endDate)) {
            return ResponseEntity.badRequest().build();
        }
        RecommendationProductPeriodStats summary = recommendationProductStatsService.getProductSummary(productId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{productId}/all-time")
    public ResponseEntity<RecommendationProductPeriodStats> getProductSummaryAllTime(@PathVariable Long productId) {

        log.info("day la ham lay thong ke chi tiet san pham duoc de xuat nhieu nhat trong tat ca thoi gian");
        RecommendationProductPeriodStats summary = recommendationProductStatsService.getProductSummaryAllTime(productId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{productId}/daily")
    public ResponseEntity<List<RecommendationProductDailyStats>> getProductDailyStats(
            @PathVariable Long productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("day la ham lay thong ke hang ngay cua 1 san pham trong 1 khoang thoi gian");
        if (isInvalidRange(startDate, endDate)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(recommendationProductStatsService.getProductDailyStats(productId, startDate, endDate));
    }

    private boolean isInvalidRange(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null && startDate.isAfter(endDate);
    }
}
