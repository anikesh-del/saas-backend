package com.anikesh.saas_backend.repository;

import com.anikesh.saas_backend.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
