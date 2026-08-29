package com.anikesh.saas_backend.repository;

import com.anikesh.saas_backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}