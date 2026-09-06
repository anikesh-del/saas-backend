package com.anikesh.saas_backend.repository;

import com.anikesh.saas_backend.entity.TenantMembership;
import com.anikesh.saas_backend.entity.TenantMembershipId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantMembershipRepository extends JpaRepository<TenantMembership, TenantMembershipId> {

    boolean existsByTenantIdAndUserId(Long tenantId, Long userId);
}