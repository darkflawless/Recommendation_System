package com.example.tracking_service.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.example.common.model.ClickEvent;
import org.example.common.model.ImpressionEvent;
import org.example.common.model.OrderEvent;
import org.example.common.model.SearchEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracking_service.service.TrackingEventPublisher;

@RestController
@RequestMapping("/tracking/events")
public class TrackingEventController {

    private final TrackingEventPublisher trackingEventPublisher;

    public TrackingEventController(TrackingEventPublisher trackingEventPublisher) {
        this.trackingEventPublisher = trackingEventPublisher;
    }

    @PostMapping("/impression")
    public ResponseEntity<Map<String, Object>> publishImpression(@RequestBody ImpressionEvent event) {
        trackingEventPublisher.publishImpression(event);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildResponse("IMPRESSION", event.getId()));
    }

    @PostMapping("/click")
    public ResponseEntity<Map<String, Object>> publishClick(@RequestBody ClickEvent event) {
        trackingEventPublisher.publishClick(event);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildResponse("CLICK", event.getId()));
    }

    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> publishSearch(@RequestBody SearchEvent event) {
        trackingEventPublisher.publishSearch(event);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildResponse("SEARCH", event.getId()));
    }

    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> publishOrder(@RequestBody OrderEvent event) {
        trackingEventPublisher.publishOrder(event);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildResponse("ORDER", event.getId()));
    }

    private Map<String, Object> buildResponse(String eventType, String eventId) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "accepted");
        response.put("eventType", eventType);
        response.put("eventId", eventId);
        response.put("receivedAt", LocalDateTime.now());
        return response;
    }
}
