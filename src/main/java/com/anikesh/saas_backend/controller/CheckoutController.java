package com.anikesh.saas_backend.controller;

import com.anikesh.saas_backend.dto.CheckoutRequestDTO;
import com.anikesh.saas_backend.dto.CheckoutResponseDTO;
import com.anikesh.saas_backend.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/billing")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponseDTO> createCheckout(@Valid @RequestBody CheckoutRequestDTO dto) {
        return ResponseEntity.ok(checkoutService.createCheckoutSession(dto.getPlanId()));
    }
}
