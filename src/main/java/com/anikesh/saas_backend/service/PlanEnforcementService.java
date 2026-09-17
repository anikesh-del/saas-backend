package com.anikesh.saas_backend.service;

import com.anikesh.saas_backend.Exception.CustomException;
import com.anikesh.saas_backend.entity.PlanFeatureId;
import com.anikesh.saas_backend.repository.PlanFeatureRepository;
import com.anikesh.saas_backend.repository.PlanRepository;
import com.anikesh.saas_backend.repository.TenantSubscriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PlanEnforcementService {

    private final TenantSubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final PlanFeatureRepository planFeatureRepository;

    public PlanEnforcementService(TenantSubscriptionRepository subscriptionRepository,
                                   PlanRepository planRepository,
                                   PlanFeatureRepository planFeatureRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.planFeatureRepository = planFeatureRepository;
    }

    public void enforceLimit(Long tenantId, String featureKey, long currentCount, String resourceName) {
        Long planId = resolveActivePlanId(tenantId);
        int limit = getIntFeature(planId, featureKey);

        if (currentCount >= limit) {
            throw new CustomException(
                    resourceName + " limit reached for your current plan (" + limit + "). Upgrade to add more.",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    private Long resolveActivePlanId(Long tenantId) {
        return subscriptionRepository
                .findFirstByTenantIdAndStatusOrderByCreatedAtDesc(tenantId, "active")
                .map(sub -> sub.getPlanId())
                .orElseGet(() -> planRepository.findByName("FREE")
                        .orElseThrow(() -> new CustomException(
                                "FREE plan not seeded — check V3__seed_billing.sql",
                                HttpStatus.INTERNAL_SERVER_ERROR))
                        .getPlanId());
    }

    private int getIntFeature(Long planId, String featureKey) {
        return planFeatureRepository.findById(new PlanFeatureId(planId, featureKey))
                .map(pf -> Integer.parseInt(pf.getFeatureValue()))
                .orElseThrow(() -> new CustomException(
                        "Feature '" + featureKey + "' not configured for this plan",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
