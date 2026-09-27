package com.anikesh.saas_backend.controller;

import com.anikesh.saas_backend.dto.CheckoutRequestDTO;
import com.anikesh.saas_backend.dto.CheckoutResponseDTO;
import com.anikesh.saas_backend.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@RestController
@RequestMapping("/api/v1/billing")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @Operation(summary = "Create a Stripe Checkout session", description = "Requires owner role in the tenant. Returns a Stripe-hosted checkout URL.")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponseDTO> createCheckout(@Valid @RequestBody CheckoutRequestDTO dto) {
        return ResponseEntity.ok(checkoutService.createCheckoutSession(dto.getPlanId()));
    }
}
