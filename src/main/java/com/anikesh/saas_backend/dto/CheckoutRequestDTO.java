package com.anikesh.saas_backend.dto;

import jakarta.validation.constraints.NotNull;

public class CheckoutRequestDTO {
    @NotNull
    private Long planId;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
}
