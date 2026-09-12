package com.anikesh.saas_backend.service;

import com.anikesh.saas_backend.Exception.CustomException;
import com.anikesh.saas_backend.dto.*;
import com.anikesh.saas_backend.entity.Task;
import com.anikesh.saas_backend.repository.*;
import com.anikesh.saas_backend.security.CurrentUserProvider;
import com.anikesh.saas_backend.tenant.RequiresRole;
import com.anikesh.saas_backend.tenant.TenantContext;
import com.anikesh.saas_backend.tenant.TenantScoped;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TenantMembershipRepository membershipRepository;
    public TaskService(TaskRepository taskRepository, TenantMembershipRepository membershipRepository) {
        this.taskRepository = taskRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    @TenantScoped
    @RequiresRole({"owner", "admin"})
    public TaskResponseDTO createTask(TaskCreateDTO dto) {
        validateTenantMember(dto.getAssignedTo());

        Task task = new Task();
        task.setTenantId(TenantContext.getTenantId());
        task.setProjectId(dto.getProjectId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus("open");
        task.setAssignedTo(dto.getAssignedTo());
        task.setDueDate(dto.getDueDate());
        return toDto(taskRepository.save(task));
    }

    @Transactional
    @TenantScoped
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    @TenantScoped
    @RequiresRole({"owner", "admin", "member"}) 
    public TaskResponseDTO updateTask(Long taskId, TaskUpdateDTO dto) {

        Task task = findOrThrow(taskId);

        Long userId = CurrentUserProvider.getCurrentUserId();
        String role = TenantContext.getRole();
        boolean isPrivileged = "owner".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role);
        boolean isOwnTask = userId.equals(task.getAssignedTo());

        if (!isPrivileged && !isOwnTask) {
            throw new CustomException("You can only update tasks assigned to you", HttpStatus.FORBIDDEN);
        }

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
        if (isPrivileged) {
            validateTenantMember(dto.getAssignedTo());
            task.setAssignedTo(dto.getAssignedTo()); 
        }
        return toDto(task);
    }

    @Transactional
    @TenantScoped
    @RequiresRole({"owner"})
    public void deleteTask(Long taskId) {
        taskRepository.delete(findOrThrow(taskId));
    }

    private Task findOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException("Task not found", HttpStatus.NOT_FOUND));
    }

    private TaskResponseDTO toDto(Task t) {
        return new TaskResponseDTO(t.getTaskId(), t.getProjectId(), t.getTenantId(), t.getTitle(),
                t.getDescription(), t.getStatus(), t.getAssignedTo(), t.getDueDate(), t.getCreatedAt(), t.getUpdatedAt());
    }

    private void validateTenantMember(Long userId) {
    membershipRepository
            .findByTenant_TenantIdAndUser_UserId(
                    TenantContext.getTenantId(),
                    userId
            )
            .orElseThrow(() ->
                    new CustomException(
                            "User is not a member of this tenant",
                            HttpStatus.BAD_REQUEST
                    )
            );
}
}
