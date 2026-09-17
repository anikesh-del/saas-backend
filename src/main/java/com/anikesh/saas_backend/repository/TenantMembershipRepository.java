package com.anikesh.saas_backend.repository;

import com.anikesh.saas_backend.entity.TenantMembership;
import com.anikesh.saas_backend.entity.TenantMembershipId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface TenantMembershipRepository extends JpaRepository<TenantMembership, TenantMembershipId> {

    boolean existsByTenant_TenantIdAndUser_UserId(Long tenantId, Long userId);
    Optional<TenantMembership> findByTenant_TenantIdAndUser_UserId(
            Long tenantId,
            Long userId
    );
    List<TenantMembership> findAllByTenant_TenantId(Long tenantId);
    long countByTenant_TenantId(Long tenantId);
}