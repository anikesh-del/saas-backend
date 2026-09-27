package com.anikesh.saas_backend.service;

import com.anikesh.saas_backend.entity.WebhookEvent;
import com.anikesh.saas_backend.repository.WebhookEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class WebhookIdempotencyservice {
    private final WebhookEventRepository webhookEventRepository;

    public WebhookIdempotencyservice(WebhookEventRepository webhookEventRepository) {
        this.webhookEventRepository = webhookEventRepository;
    }

    @Transactional
    public boolean recordIfNew(String eventId, String eventType) {
        if (webhookEventRepository.existsById(eventId)) {
            return false;
        }

        WebhookEvent webhookEvent = new WebhookEvent();
        webhookEvent.setEventId(eventId);
        webhookEvent.setEventType(eventType);
        webhookEvent.setStatus("received");
        webhookEvent.setReceivedAt(OffsetDateTime.now());

        webhookEventRepository.save(webhookEvent);
        return true;
    }
}
