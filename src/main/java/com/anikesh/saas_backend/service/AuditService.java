package com.anikesh.saas_backend.service;

import com.anikesh.saas_backend.entity.AuditLog;
import com.anikesh.saas_backend.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void record(Long tenantId, String tableName, Long rowId, String operation,
                        Long changedBy, Object oldState, Object newState) {
        AuditLog log = new AuditLog();
        log.setTenantId(tenantId);
        log.setTableName(tableName);
        log.setRowId(rowId);
        log.setOperation(operation);
        log.setChangedBy(changedBy);
        log.setChangedAt(OffsetDateTime.now());
        log.setOldData(oldState == null ? null : toJson(oldState));
        log.setNewData(toJson(newState));
        auditLogRepository.save(log);
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to serialize audit payload", e);
        }
    }
}
