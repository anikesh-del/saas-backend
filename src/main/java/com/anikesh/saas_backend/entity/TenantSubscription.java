package com.anikesh.saas_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "tenant_subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class TenantSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;

    @Column(nullable = false)
    private String status;

    @Column(name = "current_period_start")
    private OffsetDateTime currentPeriodStart;

    @Column(name = "current_period_end")
    private OffsetDateTime currentPeriodEnd;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TenantSubscription))
            return false;
        TenantSubscription that = (TenantSubscription) o;
        return subscriptionId != null && subscriptionId.equals(that.subscriptionId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
