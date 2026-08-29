package com.anikesh.saas_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Column(name = "row_id", nullable = false)
    private Long rowId;

    @Column(nullable = false)
    private String operation;

    @Column(name = "changed_by", nullable = false)
    private Long changedBy;

    @Column(name = "changed_at", nullable = false)
    private OffsetDateTime changedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_data", columnDefinition = "jsonb")
    private String oldData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_data", columnDefinition = "jsonb")
    private String newData;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditLog)) return false;
        AuditLog auditLog = (AuditLog) o;
        return auditId != null && auditId.equals(auditLog.auditId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}