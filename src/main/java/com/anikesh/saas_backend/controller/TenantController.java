package com.anikesh.saas_backend.controller;

import com.anikesh.saas_backend.entity.Tenant;
import com.anikesh.saas_backend.security.CustomUserDetails;
import com.anikesh.saas_backend.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public record CreateTenantRequest(String name) {
    }

    @Operation(summary = "Create a new tenant", description = "The authenticated user becomes the tenant's owner. No X-Tenant-Id header required — this is the bootstrap endpoint.")
    @PostMapping
    public ResponseEntity<Tenant> createTenant(@RequestBody CreateTenantRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Tenant tenant = tenantService.createTenant(req.name(), userDetails.getUserId());

        return ResponseEntity.ok(tenant);
    }
}
