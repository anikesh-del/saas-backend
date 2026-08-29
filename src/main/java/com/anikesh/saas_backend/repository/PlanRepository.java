package com.anikesh.saas_backend.repository;

import com.anikesh.saas_backend.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}
