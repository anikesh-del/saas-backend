package com.anikesh.saas_backend.controller;

import com.anikesh.saas_backend.dto.*;
import com.anikesh.saas_backend.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.util.List;

@RestController
@RequestMapping("api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Create a project", description = "Requires owner or admin role in the tenant.")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> create(@Valid @RequestBody ProjectCreateDTO dto) {
        return ResponseEntity.ok(projectService.createProject(dto));
    }

    @Operation(summary = "List all projects in the current tenant")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAll() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @Operation(summary = "Get a single project by ID")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> getOne(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProject(projectId));
    }

    @Operation(summary = "Update a project", description = "Requires owner or admin role in the tenant.")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> update(@PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateDTO dto) {
        return ResponseEntity.ok(projectService.updateProject(projectId, dto));
    }

    @Operation(summary = "Delete a project", description = "Requires owner role in the tenant.")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
