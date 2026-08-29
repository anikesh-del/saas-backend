package com.anikesh.saas_backend.repository;

import com.anikesh.saas_backend.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
}
