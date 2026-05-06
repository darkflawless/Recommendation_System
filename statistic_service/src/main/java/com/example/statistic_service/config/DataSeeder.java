package com.example.statistic_service.config;

import com.example.statistic_service.model.RecommendationProductDailyStats;
import com.example.statistic_service.repository.RecommendationProductDailyStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    @ConditionalOnProperty(
            name = "app.statistics.seed.enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    CommandLineRunner seedRecommendationStats(RecommendationProductDailyStatsRepository repository) {
        return args -> {

            LocalDate today = LocalDate.now();
            List<RecommendationProductDailyStats> seedRows = new ArrayList<>();

            // Laptop
            seedRows.add(createStat(today.minusDays(3), 1L, "Laptop Asus TUF F15", 220, 80));
            seedRows.add(createStat(today.minusDays(2), 1L, "Laptop Asus TUF F15", 250, 95));
            seedRows.add(createStat(today.minusDays(1), 1L, "Laptop Asus TUF F15", 270, 110));

            // Gaming Mouse
            seedRows.add(createStat(today.minusDays(3), 2L, "Chuot Logitech G304", 180, 70));
            seedRows.add(createStat(today.minusDays(2), 2L, "Chuot Logitech G304", 200, 85));
            seedRows.add(createStat(today.minusDays(1), 2L, "Chuot Logitech G304", 210, 90));

            // Mechanical Keyboard
            seedRows.add(createStat(today.minusDays(3), 3L, "Ban phim co Keychron K6", 160, 60));
            seedRows.add(createStat(today.minusDays(2), 3L, "Ban phim co Keychron K6", 175, 68));
            seedRows.add(createStat(today.minusDays(1), 3L, "Ban phim co Keychron K6", 190, 75));

            // Monitor
            seedRows.add(createStat(today.minusDays(3), 4L, "Man hinh LG UltraGear 27GL850", 140, 55));
            seedRows.add(createStat(today.minusDays(2), 4L, "Man hinh LG UltraGear 27GL850", 155, 60));
            seedRows.add(createStat(today.minusDays(1), 4L, "Man hinh LG UltraGear 27GL850", 170, 72));

            // Smartphone
            seedRows.add(createStat(today.minusDays(3), 5L, "iPhone 15 Pro Max", 300, 150));
            seedRows.add(createStat(today.minusDays(2), 5L, "iPhone 15 Pro Max", 340, 180));
            seedRows.add(createStat(today.minusDays(1), 5L, "iPhone 15 Pro Max", 380, 210));

            // Tablet
            seedRows.add(createStat(today.minusDays(3), 6L, "iPad Air M1", 210, 90));
            seedRows.add(createStat(today.minusDays(2), 6L, "iPad Air M1", 230, 105));
            seedRows.add(createStat(today.minusDays(1), 6L, "iPad Air M1", 250, 120));

            // Charger
            seedRows.add(createStat(today.minusDays(3), 7L, "Sac Anker 100W GaN", 190, 75));
            seedRows.add(createStat(today.minusDays(2), 7L, "Sac Anker 100W GaN", 205, 82));
            seedRows.add(createStat(today.minusDays(1), 7L, "Sac Anker 100W GaN", 220, 95));

            // Power Bank
            seedRows.add(createStat(today.minusDays(3), 8L, "Pin du phong Xiaomi 20000mAh", 170, 65));
            seedRows.add(createStat(today.minusDays(2), 8L, "Pin du phong Xiaomi 20000mAh", 185, 72));
            seedRows.add(createStat(today.minusDays(1), 8L, "Pin du phong Xiaomi 20000mAh", 200, 80));

            // Headphones
            seedRows.add(createStat(today.minusDays(3), 9L, "Tai nghe Sony WH-1000XM5", 260, 120));
            seedRows.add(createStat(today.minusDays(2), 9L, "Tai nghe Sony WH-1000XM5", 280, 135));
            seedRows.add(createStat(today.minusDays(1), 9L, "Tai nghe Sony WH-1000XM5", 310, 150));

            // Smartwatch
            seedRows.add(createStat(today.minusDays(3), 10L, "Apple Watch Series 9", 240, 110));
            seedRows.add(createStat(today.minusDays(2), 10L, "Apple Watch Series 9", 260, 125));
            seedRows.add(createStat(today.minusDays(1), 10L, "Apple Watch Series 9", 290, 140));

            repository.saveAll(seedRows);
            log.info("Reseeded {} recommendation daily stats records", seedRows.size());
        };
    }

    private RecommendationProductDailyStats createStat(
        LocalDate statDate,
        Long productId,
        String productName,
        long totalRecommendations,
        long totalViews) {
            
        RecommendationProductDailyStats stats = new RecommendationProductDailyStats();
        stats.setId(statDate + "_" + productId);
        stats.setStatDate(statDate);
        stats.setProductId(productId);
        stats.setProductName(productName);
        stats.setTotalRecommendations(totalRecommendations);
        stats.setTotalViews(totalViews);
        return stats;
    }
}