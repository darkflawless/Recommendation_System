package org.example.common.model;

import java.time.LocalDateTime;

public class ClickEvent {
    private String id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private String clickType;
    private LocalDateTime createdAt;

    public ClickEvent() {
    }

    public ClickEvent(String id,
                      Long userId,
                      Long productId,
                      String productName,
                      String productImageUrl,
                      String clickType,
                      LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.clickType = clickType;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getClickType() {
        return clickType;
    }

    public void setClickType(String clickType) {
        this.clickType = clickType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
