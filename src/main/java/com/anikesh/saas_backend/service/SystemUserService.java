package com.anikesh.saas_backend.service;

import com.anikesh.saas_backend.Exception.CustomException;
import com.anikesh.saas_backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SystemUserService {

    private static final String SYSTEM_USER_EMAIL = "system@internal";

    private final UserRepository userRepository;
    private volatile Long cachedSystemUserId;

    public SystemUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long getSystemUserId() {
        if (cachedSystemUserId == null) {
            cachedSystemUserId = userRepository.findByEmail(SYSTEM_USER_EMAIL)
                    .orElseThrow(() -> new CustomException(
                            "System user not seeded — check V3__seed_system_user.sql ran",
                            HttpStatus.INTERNAL_SERVER_ERROR))
                    .getUserId();
        }
        return cachedSystemUserId;
    }
}
