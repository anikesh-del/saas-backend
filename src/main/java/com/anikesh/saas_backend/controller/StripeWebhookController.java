package com.anikesh.saas_backend.controller;

import com.anikesh.saas_backend.service.StripeWebhookService;
import com.anikesh.saas_backend.service.WebhookIdempotencyservice;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
public class StripeWebhookController {

    private final String webhookSecret;
    private final WebhookIdempotencyservice idempotencyService;
    private final StripeWebhookService webhookService;

    public StripeWebhookController(@Value("${stripe.webhook-secret}") String webhookSecret,
                                    WebhookIdempotencyservice idempotencyService,
                                    StripeWebhookService webhookService) {
        this.webhookSecret = webhookSecret;
        this.idempotencyService = idempotencyService;
        this.webhookService = webhookService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<Void> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boolean isNewEvent = idempotencyService.recordIfNew(event.getId(), event.getType());
        if (!isNewEvent) {
            return ResponseEntity.ok().build();
        }

        try {
            webhookService.process(event);
        } catch (Exception e) {
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }
}
