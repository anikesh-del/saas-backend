package com.anikesh.saas_backend.controller;

import com.anikesh.saas_backend.dto.*;
import com.anikesh.saas_backend.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@Valid @RequestBody TaskCreateDTO dto) {
        return ResponseEntity.ok(taskService.createTask(dto));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAll() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long taskId,
                                                    @Valid @RequestBody TaskUpdateDTO dto) {
        return ResponseEntity.ok(taskService.updateTask(taskId, dto));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}