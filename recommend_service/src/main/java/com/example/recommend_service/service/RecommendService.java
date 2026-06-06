package com.example.recommend_service.service;

import com.example.recommend_service.model.UserPreference;
import com.example.recommend_service.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final UserPreferenceRepository preferenceRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${core.service.url:http://localhost:8087}")
    private String coreServiceUrl;

    private static final int TOTAL_SLOTS = 8;
    private static final int DISCOVERY_SLOTS = 4;

    // ═══════════════════════════════════════════════════════════════════════
    // PUBLIC: Lấy danh sách sản phẩm gợi ý cho user
    // Input : userId
    // Output: List sản phẩm (dạng Map key-value, ví dụ {"id":1,"name":"Chuot"...})
    // ═══════════════════════════════════════════════════════════════════════
    public List<Map<String, Object>> getRecommendations(Long userId) {

        // B2. Lấy toàn bộ tên category đang có trong hệ thống từ core service
        // VD: ["MOUSE", "LAPTOP", "KEYBOARD", "HEADSET"]
        List<String> allCategories = fetchAllCategoryNames();
        if (allCategories.isEmpty()) {
            log.warn("Không lấy được category từ core service");
            return Collections.emptyList();
        }

        // B3. Chuẩn bị danh sách kết quả
        List<Map<String, Object>> result = new ArrayList<>();

        // ── PHẦN 1: Sản phẩm từ category user ĐÃ click (theo tỷ lệ) ───────
        Map<String, Integer> clickMap = getUserPreference(userId);

        if (!clickMap.isEmpty()) {
            int personalSlots = TOTAL_SLOTS - DISCOVERY_SLOTS; // = 4 slot cho preference

            // Tính tổng số click để tính tỷ lệ phần trăm
            int totalClicks = 0;
            for (int count : clickMap.values()) {
                totalClicks += count;
            }

            // Sắp xếp category theo số click giảm dần
            // (category nhiều click nhất xếp trước)
            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(clickMap.entrySet());
            sortedEntries.sort((a, b) -> b.getValue() - a.getValue());

            for (Map.Entry<String, Integer> entry : sortedEntries) {
                String categoryName = entry.getKey(); // VD: "MOUSE"
                int clickCount = entry.getValue(); // VD: 5

                // Tính số slot tỷ lệ với số click, tối thiểu là 1
                double ratio = (double) clickCount / totalClicks;
                int slots = (int) Math.round(ratio * personalSlots);
                slots = Math.max(1, slots);

                // Gọi core service lấy sản phẩm thuộc category này
                List<Map<String, Object>> products = fetchProductsByCategory(categoryName, slots);
                result.addAll(products);

                // Đủ slot preference rồi thì dừng
                if (result.size() >= personalSlots) {
                    break;
                }
            }
        }

        // ── PHẦN 2: Discovery — category user CHƯA từng click ────────────
        // Tìm các category chưa có trong clickMap
        Set<String> clickedCategories = clickMap.keySet(); // VD: {"MOUSE", "LAPTOP"}

        List<String> unexploredCategories = new ArrayList<>();

        for (String cate : allCategories) {
            if (!clickedCategories.contains(cate)) {
                unexploredCategories.add(cate); // VD: ["KEYBOARD", "HEADSET"]
            }
        }

        // Xáo trộn để mỗi lần gợi ý discovery khác nhau
        Collections.shuffle(unexploredCategories);

        // Mỗi category chưa khám phá lấy bao nhiêu sản phẩm?
        int numUnexplored = Math.max(1, unexploredCategories.size());
        int discoveryPerCategory = Math.max(1, DISCOVERY_SLOTS / numUnexplored);

        int discoveryAdded = 0;
        for (String cate : unexploredCategories) {
            if (discoveryAdded >= DISCOVERY_SLOTS) {
                break; // đủ discovery slot rồi
            }
            List<Map<String, Object>> discoveryProducts = fetchProductsByCategory(cate, discoveryPerCategory);
            result.addAll(discoveryProducts);
            discoveryAdded += discoveryProducts.size();
        }

        log.info("userId={} → tổng {} sản phẩm ({} preference + {} discovery)",
                userId, result.size(), result.size() - discoveryAdded, discoveryAdded);

        // Giới hạn tối đa sản phẩm
        if (result.size() > TOTAL_SLOTS) {
            result = result.subList(0, TOTAL_SLOTS);
        }
        return result;
    }


    private List<String> fetchAllCategoryNames() {
        try {
            // Gọi HTTP get
            List<Map<String, Object>> categories = webClientBuilder.build()
                    .get()
                    .uri(coreServiceUrl + "/api/products/categories")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    })
                    .block(); 

            if (categories == null) {
                return Collections.emptyList();
            }

            // Lấy field "name" từ mỗi category object
            List<String> names = new ArrayList<>();
            for (Map<String, Object> category : categories) {
                Object nameValue = category.get("name");
                if (nameValue != null) {
                    names.add((String) nameValue);
                }
            }
            return names;

        } catch (Exception e) {
            log.error("Lỗi khi lấy categories từ core service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> fetchProductsByCategory(String categoryName, int limit) {
        try {
            List<Map<String, Object>> products = webClientBuilder.build()
                    .get()
                    .uri(coreServiceUrl + "/api/products/by-category?categoryName={cat}&size={size}",
                            categoryName, limit)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    })
                    .block();

            if (products == null) {
                return Collections.emptyList();
            }
            return products;

        } catch (Exception e) {
            log.error("Lỗi lấy sản phẩm category={}: {}", categoryName, e.getMessage());
            return Collections.emptyList();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PUBLIC: Lấy preference map của user từ MongoDB
    // Input : userId
    // Output: {"MOUSE": 5, "LAPTOP": 2} hoặc {} nếu user chưa click gì
    // ═══════════════════════════════════════════════════════════════════════
    public Map<String, Integer> getUserPreference(Long userId) {
        Optional<UserPreference> optionalPref = preferenceRepository.findById(userId);

        if (optionalPref.isPresent()) {
            UserPreference pref = optionalPref.get();
            return pref.getCategoryClickCount();
        } else {
            return Collections.emptyMap(); // user chưa có dữ liệu
        }
    }
}
