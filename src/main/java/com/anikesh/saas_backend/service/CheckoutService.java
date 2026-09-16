package com.anikesh.saas_backend.service;

import com.anikesh.saas_backend.Exception.CustomException;
import com.anikesh.saas_backend.dto.CheckoutResponseDTO;
import com.anikesh.saas_backend.entity.Plan;
import com.anikesh.saas_backend.repository.PlanRepository;
import com.anikesh.saas_backend.tenant.RequiresRole;
import com.anikesh.saas_backend.tenant.TenantContext;
import com.anikesh.saas_backend.tenant.TenantScoped;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service 
public class CheckoutService {
    private final PlanRepository planRepository;
    private final String successUrl;
    private final String cancelUrl;

    public CheckoutService(PlanRepository planRepository,
                            @Value("${stripe.checkout.success-url}") String successUrl,
                            @Value("${stripe.checkout.cancel-url}") String cancelUrl) {
        this.planRepository = planRepository;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
    }

    @Transactional 
    @TenantScoped 
    @RequiresRole ({"owner"})
    public CheckoutResponseDTO createCheckoutSession(Long planId){
        Long tenantId=TenantContext.getTenantId();

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException("Plan not found", HttpStatus.NOT_FOUND));
        
        if (plan.getStripePriceId() == null) {
            throw new CustomException("Plan is not configured for billing", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setClientReferenceId(tenantId.toString())
                .putMetadata("tenant_id", tenantId.toString())
                .putMetadata("plan_id", planId.toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(plan.getStripePriceId())
                                .setQuantity(1L)
                                .build())
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .build();

        try {
            Session session = Session.create(params);
            return new CheckoutResponseDTO(session.getUrl());
        } catch (StripeException e) {
            throw new CustomException("Failed to create checkout session", HttpStatus.BAD_GATEWAY);
        }
    }
}
