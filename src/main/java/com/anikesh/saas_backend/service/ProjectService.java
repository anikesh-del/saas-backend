package com.anikesh.saas_backend.service;

import com.anikesh.saas_backend.Exception.CustomException;
import com.anikesh.saas_backend.dto.ProjectCreateDTO;
import com.anikesh.saas_backend.dto.ProjectResponseDTO;
import com.anikesh.saas_backend.dto.ProjectUpdateDTO;
import com.anikesh.saas_backend.entity.Project;
import com.anikesh.saas_backend.repository.ProjectRepository;
import com.anikesh.saas_backend.security.CurrentUserProvider;
import com.anikesh.saas_backend.tenant.RequiresRole;
import com.anikesh.saas_backend.tenant.TenantContext;
import com.anikesh.saas_backend.tenant.TenantScoped;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final PlanEnforcementService planEnforcementService;
    private final AuditService auditService;

    public ProjectService(ProjectRepository projectRepository, PlanEnforcementService planEnforcementService,
            AuditService auditService) {
        this.projectRepository = projectRepository;
        this.planEnforcementService = planEnforcementService;
        this.auditService = auditService;

    }

    @Transactional
    @TenantScoped
    @RequiresRole({ "owner", "admin" })
    public ProjectResponseDTO createProject(ProjectCreateDTO dto) {

        Long tenantId = TenantContext.getTenantId();
        planEnforcementService.enforceLimit(tenantId, "max_projects", projectRepository.count(), "Project");

        Project project = new Project();
        project.setTenantId(TenantContext.getTenantId());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStatus("active");
        project.setCreatedBy(CurrentUserProvider.getCurrentUserId());
        project = projectRepository.save(project);

        auditService.record(tenantId, "projects", project.getProjectId(), "INSERT",
                project.getCreatedBy(), null, toDto(project));

        return toDto(project);
    }

    @Transactional
    @TenantScoped
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    @TenantScoped
    public ProjectResponseDTO getProject(Long projectId) {
        Project project = findOrThrow(projectId);
        return toDto(project);
    }

    @Transactional
    @TenantScoped
    @RequiresRole({ "owner", "admin" })
    public ProjectResponseDTO updateProject(Long projectId, ProjectUpdateDTO dto) {
        Project project = findOrThrow(projectId);
        ProjectResponseDTO before = toDto(project);

    project.setName(dto.getName());
    project.setDescription(dto.getDescription());
    project.setStatus(dto.getStatus());

    auditService.record(TenantContext.getTenantId(), "projects", project.getProjectId(), "UPDATE",
            CurrentUserProvider.getCurrentUserId(), before, toDto(project));

    return toDto(project);
    }

    @Transactional
    @TenantScoped
    @RequiresRole({ "owner" })
    public void deleteProject(Long projectId) {

    Project project = findOrThrow(projectId);

    ProjectResponseDTO before = toDto(project);

    Long tenantId = TenantContext.getTenantId();
    Long userId = CurrentUserProvider.getCurrentUserId();

    projectRepository.delete(project);

    auditService.record(
            tenantId,
            "projects",
            projectId,
            "DELETE",
            userId,
            before,
            null
    );
    }

    private Project findOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException("Project not found", HttpStatus.NOT_FOUND));
    }

    private ProjectResponseDTO toDto(Project p) {
        return new ProjectResponseDTO(p.getProjectId(), p.getTenantId(), p.getName(),
                p.getDescription(), p.getStatus(), p.getCreatedBy(), p.getCreatedAt(), p.getUpdatedAt());
    }
}
