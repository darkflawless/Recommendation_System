package com.example.recommend_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Lưu preference của 1 user: category nào được click nhiều nhất.
 * Document ID = userId để upsert dễ dàng.
 */
@Data
@Document(collection = "user_preferences")
public class UserPreference {

    @Id
    private Long userId;

    @Indexed
    private Map<String, Integer> categoryClickCount = new HashMap<>();

    private LocalDateTime lastUpdated;

    public void incrementCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) return;
        categoryClickCount.merge(categoryName, 1, Integer::sum);
        lastUpdated = LocalDateTime.now();
    }

    public String getTopCategory() {
        return categoryClickCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
