package com.anikesh.saas_backend.repository;

import com.anikesh.saas_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
