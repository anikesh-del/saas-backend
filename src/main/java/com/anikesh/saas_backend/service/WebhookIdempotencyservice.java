package com.anikesh.saas_backend.service;

import com.anikesh.saas_backend.entity.WebhookEvent;
import com.anikesh.saas_backend.repository.WebhookEventRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class WebhookIdempotencyservice {
    private final WebhookEventRepository webhookEventRepository;

    public WebhookIdempotencyservice(WebhookEventRepository webhookEventRepository) {
        this.webhookEventRepository = webhookEventRepository;
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public boolean recordIfNew(String eventId, String eventType){
        WebhookEvent webhookEvent=new WebhookEvent();
        webhookEvent.setEventId(eventId);
        webhookEvent.setEventType(eventType);
        webhookEvent.setStatus("received");
        webhookEvent.setReceivedAt(OffsetDateTime.now());

        try {
            webhookEventRepository.saveAndFlush(webhookEvent);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}
