package com.anikesh.saas_backend.repository;

import com.anikesh.saas_backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
