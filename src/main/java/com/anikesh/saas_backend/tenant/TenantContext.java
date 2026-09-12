package com.anikesh.saas_backend.tenant;

public class TenantContext {
    private static final ThreadLocal<Long> CURRENT_TENANT=new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE=new ThreadLocal<>();

    public static void setTenantId(Long tenantId){
        CURRENT_TENANT.set(tenantId);
    }

    public static Long getTenantId(){
        return CURRENT_TENANT.get();
    }

    public static void setRole(String role){
        ROLE.set(role);
    }

    public static String getRole(){
        return ROLE.get();
    }

    public static void clear(){
        CURRENT_TENANT.remove();
        ROLE.remove();
    }
}
