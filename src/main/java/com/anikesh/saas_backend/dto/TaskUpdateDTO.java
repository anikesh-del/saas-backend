package com.anikesh.saas_backend.dto;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class TaskUpdateDTO {
    @NotBlank private String title;
    private String description;
    @NotBlank private String status;
    private Long assignedTo;
    private LocalDate dueDate;

    //getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
