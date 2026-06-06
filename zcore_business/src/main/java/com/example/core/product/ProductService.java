package com.example.core.product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${recommend.service.url:http://localhost:8096}")
    private String recommendServiceUrl;

    public List<Product> getRecommendationsForCustomer(Long customerId) {
        log.info("Lấy recommendations cho customerId={}", customerId);
        try {
            List<Map<String, Object>> raw = webClientBuilder.build()
                    .get()
                    .uri(recommendServiceUrl + "/recommend/products?userId={id}", customerId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    })
                    .block();

            if (raw == null || raw.isEmpty()) {
                log.info("Recommend service không trả data");
                return productRepository.findAll(PageRequest.of(0, 5)).getContent();
            }

            List<Long> ids = raw.stream()
                    .map(m -> {
                        Object idObj = m.get("id");
                        if (idObj instanceof Number)
                            return ((Number) idObj).longValue();
                        return null;
                    })
                    .filter(id -> id != null)
                    .toList();

            return productRepository.findByIdIn(ids);

        } catch (Exception e) {
            log.error("Lỗi khi gọi recommend_service: {}, fallback...", e.getMessage());
            return productRepository.findAll(PageRequest.of(0, 5)).getContent();
        }
    }

    public List<Product> searchProducts(String keyword, int page, int size) {
        log.info("Tìm kiếm sản phẩm: keyword={}, page={}, size={}", keyword, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    public List<Product> getProductsByCategory(String categoryName, int size) {
        log.info("Lấy sản phẩm theo category={}, size={}", categoryName, size);
        Pageable pageable = PageRequest.of(0, size);
        return productRepository.findByCategoryNameIgnoreCase(categoryName, pageable);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
}
