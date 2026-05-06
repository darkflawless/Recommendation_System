package com.example.statistic_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Document(collection = "recommendation_product_daily_stats")
@CompoundIndexes({
        @CompoundIndex(name = "uk_stat_date_product", def = "{'statDate': 1, 'productId': 1}", unique = true)
})
public class RecommendationProductDailyStats implements ViewRateStat {

    @Id
    private String id;

    private LocalDate statDate;

    private Long productId;

    private String productName;

    private long totalRecommendations;

    private long totalViews;

}
