package com.example.recommend_service.listener;

import com.example.recommend_service.model.UserPreference;
import com.example.recommend_service.repository.UserPreferenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.example.common.model.ClickEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
public class ClickEventListener {

    private final UserPreferenceRepository preferenceRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${core.service.url:http://localhost:8087}")
    private String coreServiceUrl;

    public ClickEventListener(UserPreferenceRepository preferenceRepository,
                               WebClient.Builder webClientBuilder) {
        this.preferenceRepository = preferenceRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @RabbitListener(queues = "#{@clickQueue.name}")
    public void handleClickEvent(ClickEvent event) {
        log.info("[RECOMMEND] Nhận click event: userId={}, productId={}, category={}",
                event.getUserId(), event.getProductId(), event.getCategoryName());

        if (event.getUserId() == null) {
            log.warn("[RECOMMEND] Bỏ qua event vì thiếu userId");
            return;
        }

        // Nếu categoryName null → fallback: gọi core service lấy category theo productId
        String categoryName = event.getCategoryName();
        if (categoryName == null && event.getProductId() != null) {
            categoryName = fetchCategoryByProductId(event.getProductId());
            if (categoryName != null) {
                log.info("[RECOMMEND] Đã resolve category từ core service: productId={} → {}",
                        event.getProductId(), categoryName);
            }
        }

        if (categoryName == null || categoryName.isBlank()) {
            log.warn("[RECOMMEND] Không xác định được category cho productId={}, bỏ qua event",
                    event.getProductId());
            return;
        }

        // Upsert UserPreference
        UserPreference pref = preferenceRepository.findById(event.getUserId())
                .orElseGet(() -> {
                    UserPreference newPref = new UserPreference();
                    newPref.setUserId(event.getUserId());
                    return newPref;
                });

        pref.incrementCategory(categoryName);
        preferenceRepository.save(pref);

        log.info("[RECOMMEND] Đã cập nhật preference userId={}: {}", event.getUserId(), pref.getCategoryClickCount());
    }

    /**
     * Gọi core service để lấy categoryName của một product.
     * Endpoint: GET /api/products/{id}
     * Response field mong đợi: {"categoryName": "MOUSE"} hoặc {"category": {"name": "MOUSE"}}
     */
    @SuppressWarnings("unchecked")
    private String fetchCategoryByProductId(Long productId) {
        try {
            Map<String, Object> product = webClientBuilder.build()
                    .get()
                    .uri(coreServiceUrl + "/api/products/" + productId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (product == null) return null;

            // Thử lấy field "categoryName" trực tiếp
            if (product.get("categoryName") instanceof String s) return s;

            // Thử lấy nested: category.name
            if (product.get("category") instanceof Map<?, ?> cat) {
                Object name = cat.get("name");
                if (name instanceof String s) return s;
            }

            return null;
        } catch (Exception e) {
            log.error("[RECOMMEND] Lỗi khi lấy category từ core service cho productId={}: {}",
                    productId, e.getMessage());
            return null;
        }
    }
}
