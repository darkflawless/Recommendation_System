package com.example.recommend_service.repository;

import com.example.recommend_service.model.UserPreference;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserPreferenceRepository extends MongoRepository<UserPreference, Long> {
}
