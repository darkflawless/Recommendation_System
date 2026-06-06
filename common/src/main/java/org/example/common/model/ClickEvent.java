package org.example.common.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ClickEvent {
    private String id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Long categoryId;
    private String categoryName;
    private String clickType;
    private LocalDateTime createdAt;

    public ClickEvent() {
    }

    public ClickEvent(String id,
            Long userId,
            Long productId,
            String productName,
            String productImageUrl,
            Long categoryId,
            String categoryName,
            String clickType,
            LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.clickType = clickType;
        this.createdAt = createdAt;
    }

}
