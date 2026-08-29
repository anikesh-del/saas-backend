package com.anikesh.saas_backend.repository;

import com.anikesh.saas_backend.entity.TenantSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantSubscriptionRepository extends JpaRepository<TenantSubscription, Long> {
}