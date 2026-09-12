package com.anikesh.saas_backend.dto;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record TaskResponseDTO(
    Long taskId, Long projectId, Long tenantId, String title, String description,
    String status, Long assignedTo, LocalDate dueDate, OffsetDateTime createdAt, OffsetDateTime updatedAt
) {}
