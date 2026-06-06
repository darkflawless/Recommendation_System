package com.example.core.product;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.core.auth.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/recommendations")
    public ResponseEntity<List<Product>> getRecommendationsForCustomer(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long customerId = userPrincipal.getId();
        log.info("Lấy recommendations cho customerId={}", customerId);
        return ResponseEntity.ok(productService.getRecommendationsForCustomer(customerId));
    }

    // Recommend_service gọi để resolve category khi click event thiếu categoryName
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        log.info("Lấy product theo id={}", id);
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── GET /api/products/search?keyword=&page=0&size=10 ──────────────────
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Tìm kiếm: keyword={}, page={}, size={}", keyword, page, size);
        return ResponseEntity.ok(productService.searchProducts(keyword, page, size));
    }

    // ── GET /api/products/by-category?categoryName=MOUSE&size=4 ──────────
    // Recommend_service gọi vào đây để lấy product theo category
    @GetMapping("/by-category")
    public ResponseEntity<List<Product>> getByCategory(
            @RequestParam String categoryName,
            @RequestParam(defaultValue = "4") int size) {
        log.info("Lấy sản phẩm theo category={}, size={}", categoryName, size);
        return ResponseEntity.ok(productService.getProductsByCategory(categoryName, size));
    }

    // ── GET /api/categories ───────────────────────────────────────────────
    // Recommend_service gọi để biết tất cả category hiện có (discovery slot)
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        log.info("Lấy tất cả categories");
        return ResponseEntity.ok(productService.getAllCategories());
    }
}
