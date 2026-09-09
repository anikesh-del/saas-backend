package com.anikesh.saas_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional;

import com.anikesh.saas_backend.Exception.CustomException;
import com.anikesh.saas_backend.entity.*;
import com.anikesh.saas_backend.repository.*;

import java.util.*;
import java.time.OffsetDateTime;
@Service
public class TenantService {
    
    private final TenantRepository tenantRepository;
    private final TenantMembershipRepository membershipRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public TenantService(TenantRepository tenantRepository, TenantMembershipRepository membershipRepository, RoleRepository roleRepository, UserRepository userRepository){
        this.tenantRepository = tenantRepository; this.membershipRepository = membershipRepository; this.roleRepository = roleRepository; this.userRepository = userRepository;
    }

    @Transactional
    public Tenant createTenant(String name , Long userId){
        User user=userRepository.findById(userId).orElseThrow(() -> new CustomException( "User not found", HttpStatus.NOT_FOUND ));

        Role ownerRole = roleRepository.findByName("owner") .orElseThrow(() -> new CustomException( "Owner role not found", HttpStatus.INTERNAL_SERVER_ERROR ));

        Tenant tenant = new Tenant(); 
        tenant.setName(name);
        tenant.setStatus("active"); 
        tenant = tenantRepository.save(tenant);

        TenantMembership membership = new TenantMembership();

        membership.setId( new TenantMembershipId( tenant.getTenantId(), user.getUserId() )
    );
        membership.setTenant(tenant);
        membership.setUser(user); 
        membership.setRole(ownerRole); 
        membership.setJoinedAt(OffsetDateTime.now()); 
        membershipRepository.save(membership); return tenant;
    }
}
