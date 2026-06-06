package com.example.tracking_service.service;

import org.example.common.model.ClickEvent;
import org.example.common.model.ImpressionEvent;
import org.example.common.model.OrderEvent;
import org.example.common.model.SearchEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TrackingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${tracking.publish.rabbit.enabled:true}")
    private boolean rabbitEnabled;

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

    public TrackingEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishClick(ClickEvent event) {
        publish(event, clickRoutingKey);
    }

    public void publishImpression(ImpressionEvent event) {
        publish(event, impressionRoutingKey);
    }

    public void publishSearch(SearchEvent event) {
        publish(event, searchRoutingKey);
    }

    public void publishOrder(OrderEvent event) {
        publish(event, orderRoutingKey);
    }

    private void publish(Object event, String routingKey) {
        if (rabbitEnabled) {
            rabbitTemplate.convertAndSend(trackingExchange, routingKey, event);
        }
    }


}
