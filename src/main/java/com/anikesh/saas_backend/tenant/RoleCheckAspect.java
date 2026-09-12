package com.anikesh.saas_backend.tenant;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import com.anikesh.saas_backend.Exception.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Aspect 
@Component
@Order (150)
public class RoleCheckAspect {
    
    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint pjp, RequiresRole requireRole) throws Throwable {
        String[] requiredRoles = requireRole.value();
        String userRole = TenantContext.getRole();

        if (userRole == null) {
            throw new CustomException(
                    "No role found in tenant context",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        boolean allowed = Arrays.stream(requiredRoles)
                .anyMatch(role -> role.equalsIgnoreCase(userRole));

        if (!allowed) {
            throw new CustomException(
                    "User does not have the required role",
                    HttpStatus.FORBIDDEN
            );
        }

        return pjp.proceed();
    }
}
