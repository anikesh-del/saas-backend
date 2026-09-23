package com.anikesh.saas_backend.dto;
import jakarta.validation.constraints.NotNull;

public class MemberAddDTO {
    @NotNull private String email;
    @NotNull private String role; // "admin" or "member" only - see service
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
