package com.anikesh.saas_backend.tenant;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.anikesh.saas_backend.Exception.CustomException;

@Aspect 
@Component 
@Order(200)
public class TenanatScopeAspect {
    private final JdbcTemplate jdbcTemplate;

    TenanatScopeAspect(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Around("@annotation(com.anikesh.saas_backend.tenant.TenantScoped)")
    public Object applyTenantContext(ProceedingJoinPoint pjp) throws Throwable {

        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new CustomException(
                "TenantScoped method called outside an active transaction",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        Long tenantId=TenantContext.getTenantId();
        if (tenantId == null) {
            throw new CustomException(
                "No tenant context set for TenantScoped method",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

                jdbcTemplate.execute("SET LOCAL app.current_tenant_id = '" + tenantId + "'");

        return pjp.proceed();
    }
}
