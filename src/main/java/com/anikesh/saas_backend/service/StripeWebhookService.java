package com.anikesh.saas_backend.service;

import com.anikesh.saas_backend.entity.AuditLog;
import com.anikesh.saas_backend.entity.TenantSubscription;
import com.anikesh.saas_backend.repository.AuditLogRepository;
import com.anikesh.saas_backend.repository.TenantSubscriptionRepository;
import com.anikesh.saas_backend.repository.WebhookEventRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class StripeWebhookService {

    private final TenantSubscriptionRepository subscriptionRepository;
    private final WebhookEventRepository webhookEventRepository;
    private final AuditLogRepository auditLogRepository;
    private final SystemUserService systemUserService;
    private final ObjectMapper objectMapper;

    public StripeWebhookService(TenantSubscriptionRepository subscriptionRepository,
            WebhookEventRepository webhookEventRepository, AuditLogRepository auditLogRepository,
            SystemUserService systemUserService, ObjectMapper objectMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.webhookEventRepository = webhookEventRepository;
        this.auditLogRepository = auditLogRepository;
        this.systemUserService = systemUserService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void process(Event event) {
        switch (event.getType()) {
            case "checkout.session.completed" -> handleCheckoutCompleted(event);
            case "customer.subscription.updated" -> handleSubscriptionUpdated(event);
            case "customer.subscription.deleted" -> handleSubscriptionDeleted(event);
            case "invoice.paid" -> handleInvoicePaid();
            case "invoice.payment_failed" -> handleInvoicePaymentFailed();
            default -> {
                /* ignore event types we don't act on */ }
        }
        markProcessed(event.getId());
    }

    private void handleCheckoutCompleted(Event event) {
        Session session = (Session) deserialize(event);
        Long tenantId = Long.valueOf(session.getClientReferenceId());
        Long planId = Long.valueOf(session.getMetadata().get("plan_id"));

        TenantSubscription subscription = new TenantSubscription();
        subscription.setTenantId(tenantId);
        subscription.setPlanId(planId);
        subscription.setStripeCustomerId(session.getCustomer());
        subscription.setStripeSubscriptionId(session.getSubscription());
        subscription.setStatus("active");
        subscription = subscriptionRepository.save(subscription);

        writeAudit(tenantId, subscription.getSubscriptionId(), "INSERT", null, subscription);
    }

    private void handleSubscriptionUpdated(Event event) {
        Subscription stripeSub = (Subscription) deserialize(event);
        TenantSubscription subscription = findByStripeSubscription(stripeSub.getId());

        String before = toJson(subscription);

        subscription.setStatus(stripeSub.getStatus());
        // current_period_start/end moved off Subscription onto SubscriptionItem
        // as of the Basil API release — read it from the (single) item.
        var item = stripeSub.getItems().getData().get(0);
        subscription.setCurrentPeriodStart(toOffsetDateTime(item.getCurrentPeriodStart()));
        subscription.setCurrentPeriodEnd(toOffsetDateTime(item.getCurrentPeriodEnd()));

        writeAudit(subscription.getTenantId(), subscription.getSubscriptionId(), "UPDATE", before, subscription);
    }

    private void handleSubscriptionDeleted(Event event) {
        Subscription stripeSub = (Subscription) deserialize(event);
        TenantSubscription subscription = findByStripeSubscription(stripeSub.getId());

        String before = toJson(subscription);
        subscription.setStatus("canceled");

        writeAudit(subscription.getTenantId(), subscription.getSubscriptionId(), "UPDATE", before, subscription);
    }

    private void handleInvoicePaid() {
        // No invoice state tracked in V1.
    }

    private void handleInvoicePaymentFailed() {
        // No invoice state tracked in V1.
    }

    private TenantSubscription findByStripeSubscription(String stripeSubscriptionId) {
        return subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId)
                .orElseThrow(() -> new IllegalStateException(
                        "No tenant_subscription found for Stripe subscription " + stripeSubscriptionId));
    }

    private void markProcessed(String eventId) {
        webhookEventRepository.findById(eventId).ifPresent(we -> {
            we.setStatus("processed");
            we.setProcessedAt(OffsetDateTime.now());
        });
    }

    private void writeAudit(Long tenantId, Long subscriptionId, String operation, Object oldState, Object newState) {
        AuditLog log = new AuditLog();
        log.setTenantId(tenantId);
        log.setTableName("tenant_subscriptions");
        log.setRowId(subscriptionId);
        log.setOperation(operation);
        log.setChangedBy(systemUserService.getSystemUserId());
        log.setChangedAt(OffsetDateTime.now());
        log.setOldData(oldState == null ? null : toJson(oldState));
        log.setNewData(toJson(newState));
        auditLogRepository.save(log);
    }

    private StripeObject deserialize(Event event) {
        return event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new IllegalStateException(
                        "Unable to deserialize Stripe event: " + event.getId()));
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to serialize audit payload", e);
        }
    }

    private OffsetDateTime toOffsetDateTime(Long epochSeconds) {
        return epochSeconds == null ? null
                : OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC);
    }
}
