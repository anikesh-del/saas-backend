package com.anikesh.saas_backend.repository;

import com.anikesh.saas_backend.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, String> {
}
