package com.anikesh.saas_backend.controller;

import com.anikesh.saas_backend.dto.*;
import com.anikesh.saas_backend.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> create(@Valid @RequestBody ProjectCreateDTO dto) {
        return ResponseEntity.ok(projectService.createProject(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAll() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> getOne(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProject(projectId));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> update(@PathVariable Long projectId,
                                                       @Valid @RequestBody ProjectUpdateDTO dto) {
        return ResponseEntity.ok(projectService.updateProject(projectId, dto));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
