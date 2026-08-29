package com.anikesh.saas_backend.repository;

import com.anikesh.saas_backend.entity.PlanFeature;
import com.anikesh.saas_backend.entity.PlanFeatureId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanFeatureRepository extends JpaRepository<PlanFeature, PlanFeatureId> {
}
