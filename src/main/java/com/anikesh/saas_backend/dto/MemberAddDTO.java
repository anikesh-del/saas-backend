package com.anikesh.saas_backend.dto;
import jakarta.validation.constraints.NotNull;

public class MemberAddDTO {
    @NotNull private Long userId;
    @NotNull private String role; // "admin" or "member" only - see service
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
