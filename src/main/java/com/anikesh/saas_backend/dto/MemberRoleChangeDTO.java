package com.anikesh.saas_backend.dto;
import jakarta.validation.constraints.NotBlank;

public class MemberRoleChangeDTO {
    @NotBlank private String role;
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
