package com.anikesh.saas_backend.controller;

import com.anikesh.saas_backend.dto.*;
import com.anikesh.saas_backend.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Create a task", description = "Requires owner or admin role. assignedTo is optional.")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@Valid @RequestBody TaskCreateDTO dto) {
        return ResponseEntity.ok(taskService.createTask(dto));
    }

    @Operation(summary = "List all tasks in the current tenant")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAll() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @Operation(summary = "Update a task", description = "Members may update their own assigned tasks but cannot reassign; only owner/admin can reassign.")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateDTO dto) {
        return ResponseEntity.ok(taskService.updateTask(taskId, dto));
    }

    @Operation(summary = "Delete a task", description = "Requires owner role in the tenant.")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}