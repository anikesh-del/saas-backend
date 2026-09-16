package com.anikesh.saas_backend.repository;

import com.anikesh.saas_backend.entity.TenantSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TenantSubscriptionRepository extends JpaRepository<TenantSubscription, Long> {
     Optional<TenantSubscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}