package com.example.tracking_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.common.model.ClickEvent;
import org.example.common.model.ImpressionEvent;
import org.example.common.model.OrderEvent;
import org.example.common.model.SearchEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TrackingEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${tracking.publish.rabbit.enabled:true}")
    private boolean rabbitEnabled;

    @Value("${tracking.publish.kafka.enabled:true}")
    private boolean kafkaEnabled;

    @Value("${tracking.kafka.topic.click:tracking.click.events}")
    private String clickTopic;

    @Value("${tracking.kafka.topic.impression:tracking.impression.events}")
    private String impressionTopic;

    @Value("${tracking.kafka.topic.search:tracking.search.events}")
    private String searchTopic;

    @Value("${tracking.kafka.topic.order:tracking.order.events}")
    private String orderTopic;

    @Value("${tracking.rabbit.exchange:tracking.events}")
    private String trackingExchange;

    @Value("${tracking.rabbit.routing.click:tracking.click}")
    private String clickRoutingKey;

    @Value("${tracking.rabbit.routing.impression:tracking.impression}")
    private String impressionRoutingKey;

    @Value("${tracking.rabbit.routing.search:tracking.search}")
    private String searchRoutingKey;

    @Value("${tracking.rabbit.routing.order:tracking.order}")
    private String orderRoutingKey;

    public TrackingEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, RabbitTemplate rabbitTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishClick(ClickEvent event) {
        fillCommonFields(event);
        publish(event, clickTopic, clickRoutingKey, event.getId());
    }

    public void publishImpression(ImpressionEvent event) {
        fillCommonFields(event);
        publish(event, impressionTopic, impressionRoutingKey, event.getId());
    }

    public void publishSearch(SearchEvent event) {
        fillCommonFields(event);
        publish(event, searchTopic, searchRoutingKey, event.getId());
    }

    public void publishOrder(OrderEvent event) {
        fillCommonFields(event);
        publish(event, orderTopic, orderRoutingKey, event.getId());
    }

    private void publish(Object event, String topic, String routingKey, String key) {
        if (kafkaEnabled) {
            kafkaTemplate.send(topic, key, event);
        }

        if (rabbitEnabled) {
            rabbitTemplate.convertAndSend(trackingExchange, routingKey, event);
        }
    }

    private void fillCommonFields(ClickEvent event) {
        if (event.getId() == null || event.getId().isBlank()) {
            event.setId(UUID.randomUUID().toString());
        }
        if (event.getCreatedAt() == null) {
            event.setCreatedAt(LocalDateTime.now());
        }
    }

    private void fillCommonFields(ImpressionEvent event) {
        if (event.getId() == null || event.getId().isBlank()) {
            event.setId(UUID.randomUUID().toString());
        }
        if (event.getCreatedAt() == null) {
            event.setCreatedAt(LocalDateTime.now());
        }
    }

    private void fillCommonFields(SearchEvent event) {
        if (event.getId() == null || event.getId().isBlank()) {
            event.setId(UUID.randomUUID().toString());
        }
        if (event.getCreatedAt() == null) {
            event.setCreatedAt(LocalDateTime.now());
        }
    }

    private void fillCommonFields(OrderEvent event) {
        if (event.getId() == null || event.getId().isBlank()) {
            event.setId(UUID.randomUUID().toString());
        }
        if (event.getCreatedAt() == null) {
            event.setCreatedAt(LocalDateTime.now());
        }
    }
}
