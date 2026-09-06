package com.anikesh.saas_backend.controller;

import com.anikesh.saas_backend.entity.Tenant;
import com.anikesh.saas_backend.security.CustomUserDetails;
import com.anikesh.saas_backend.service.TenantService; 
import org.springframework.http.ResponseEntity; import org.springframework.security.core.annotation.AuthenticationPrincipal; 
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenants")
public class TenantController {
    
    private final TenantService tenantService;

    public  TenantController(TenantService tenantService){
        this.tenantService=tenantService;
    }

    public record CreateTenantRequest(String name){}

    @PostMapping
    public ResponseEntity<Tenant> createTenant(@RequestBody CreateTenantRequest req, @AuthenticationPrincipal CustomUserDetails userDetails){
          
        Tenant tenant = tenantService.createTenant( req.name(), userDetails.getUserId() );

        return ResponseEntity.ok(tenant);
    }
}
