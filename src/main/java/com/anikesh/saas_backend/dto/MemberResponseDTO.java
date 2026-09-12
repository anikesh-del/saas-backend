package com.anikesh.saas_backend.dto;
import java.time.OffsetDateTime;

public record MemberResponseDTO(
    Long userId, String name, String email, String role, OffsetDateTime joinedAt
) {}