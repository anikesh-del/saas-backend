package com.anikesh.saas_backend.dto;
import java.time.OffsetDateTime;

public record ProjectResponseDTO(
    Long projectId, Long tenantId, String name, String description,
    String status, Long createdBy, OffsetDateTime createdAt, OffsetDateTime updatedAt
) {}
