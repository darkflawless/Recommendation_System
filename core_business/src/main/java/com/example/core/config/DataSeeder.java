package com.example.core.config;

import com.example.core.model.Customer;
import com.example.core.model.Order;
import com.example.core.model.OrderDetail;
import com.example.core.model.Product;
import com.example.core.model.SearchHistory;
import com.example.core.model.Status;
import com.example.core.repository.CustomerRepository;
import com.example.core.repository.OrderDetailRepository;
import com.example.core.repository.OrderRepository;
import com.example.core.repository.ProductRepository;
import com.example.core.repository.SearchHistoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(CustomerRepository customerRepository,
                               OrderRepository orderRepository,
                               OrderDetailRepository orderDetailRepository,
                               ProductRepository productRepository,
                               SearchHistoryRepository searchHistoryRepository) {
        return args -> {
            if (customerRepository.count() == 0) {
                Customer c1 = new Customer();
                c1.setName("Le Thi Mai");
                c1.setEmail("mai.le@example.com");
                c1.setPassword("123456");
                c1.setPhone("0901000015");
                c1.setMemStatus("PLATINUM");

                Customer c2 = new Customer();
                c2.setName("Pham Quang Huy");
                c2.setEmail("huy.pham@example.com");
                c2.setPassword("123456");
                c2.setPhone("0901000026");
                c2.setMemStatus("GOLD");


                c1 = customerRepository.save(c1);
                c2 = customerRepository.save(c2);

                Product p1 = new Product();
                p1.setName("Laptop Lenovo Legion 5");
                p1.setDescription("Laptop gaming tam trung, man 15.6 inch");
                p1.setPrice(23500000.0);
                p1.setImageUrl("https://example.com/lenovo-legion-5.jpg");

                Product p2 = new Product();
                p2.setName("Chuot Razer Orochi V2");
                p2.setDescription("Chuot khong day nhe, pin lau");
                p2.setPrice(950000.0);
                p2.setImageUrl("https://example.com/razer-orochi-v2.jpg");

                Product p3 = new Product();
                p3.setName("Ban phim co Keychron K2");
                p3.setDescription("Ban phim co layout 75%, switch brown");
                p3.setPrice(1950000.0);
                p3.setImageUrl("https://example.com/keychron-k2.jpg");


                p1 = productRepository.save(p1);
                p2 = productRepository.save(p2);
                p3 = productRepository.save(p3);

                Order o1 = new Order();
                o1.setCustomer(c1);
                o1.setOrderDate(LocalDateTime.now().minusDays(3));
                o1.setStatus(Status.DELIVERED);
                o1.setNote("Don hang seed cho customer 1 - lan 2");

                Order o2 = new Order();
                o2.setCustomer(c2);
                o2.setOrderDate(LocalDateTime.now().minusDays(1));
                o2.setStatus(Status.CONFIRMED);
                o2.setNote("Don hang seed cho customer 2 - lan 2");

                o1 = orderRepository.save(o1);
                o2 = orderRepository.save(o2);

                OrderDetail od1 = new OrderDetail();
                od1.setOrder(o1);
                od1.setProduct(p1);
                od1.setQuantity(1);
                od1.setUnitPrice(23500000.0);
                od1.setIsFromRecommendation(false);

                OrderDetail od2 = new OrderDetail();
                od2.setOrder(o2);
                od2.setProduct(p2);
                od2.setQuantity(1);
                od2.setUnitPrice(1950000.0);
                od2.setIsFromRecommendation(true);

                orderDetailRepository.save(od1);
                orderDetailRepository.save(od2);

                SearchHistory s1 = new SearchHistory();
                s1.setCustomer(c1);
                s1.setKeyword("laptop lenovo legion");
                s1.setCreatedAt(LocalDateTime.now().minusHours(12));

                SearchHistory s2 = new SearchHistory();
                s2.setCustomer(c1);
                s2.setKeyword("chuot razer orochi");
                s2.setCreatedAt(LocalDateTime.now().minusHours(7));

                SearchHistory s3 = new SearchHistory();
                s3.setCustomer(c2);
                s3.setKeyword("ban phim keychron k2");
                s3.setCreatedAt(LocalDateTime.now().minusHours(5));

                searchHistoryRepository.save(s1);
                searchHistoryRepository.save(s2);
                searchHistoryRepository.save(s3);
            }

            Product product1 = productRepository.findById(1L).orElseGet(() -> {
                Product p = new Product();
                p.setName("Seed Product for ID 1");
                p.setDescription("Fallback product for order #1");
                p.setPrice(100000.0);
                p.setImageUrl("https://example.com/seed-product-1.jpg");
                return productRepository.save(p);
            });

            Product product2 = productRepository.findById(2L).orElseGet(() -> {
                Product p = new Product();
                p.setName("Seed Product for ID 2");
                p.setDescription("Fallback product for order #2");
                p.setPrice(120000.0);
                p.setImageUrl("https://example.com/seed-product-2.jpg");
                return productRepository.save(p);
            });

            orderRepository.findById(1L).ifPresent(order -> {
                if (orderDetailRepository.findByOrderId(order.getId()).isEmpty()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrder(order);
                    detail.setProduct(product1);
                    detail.setQuantity(1);
                    detail.setUnitPrice(product1.getPrice());
                    detail.setIsFromRecommendation(false);
                    orderDetailRepository.save(detail);
                }
            });

            orderRepository.findById(2L).ifPresent(order -> {
                if (orderDetailRepository.findByOrderId(order.getId()).isEmpty()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrder(order);
                    detail.setProduct(product2);
                    detail.setQuantity(1);
                    detail.setUnitPrice(product2.getPrice());
                    detail.setIsFromRecommendation(true);
                    orderDetailRepository.save(detail);
                }
            });
        };
    }
}
