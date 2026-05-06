package org.example.common.model;

import java.time.LocalDateTime;

public class SearchEvent {
    private String id;
    private Long userId;
    private String keyword;
    private Integer resultsCount;
    private LocalDateTime createdAt;

    public SearchEvent() {
    }

    public SearchEvent(String id, Long userId, String keyword, Integer resultsCount, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.keyword = keyword;
        this.resultsCount = resultsCount;
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getResultsCount() {
        return resultsCount;
    }

    public void setResultsCount(Integer resultsCount) {
        this.resultsCount = resultsCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
 