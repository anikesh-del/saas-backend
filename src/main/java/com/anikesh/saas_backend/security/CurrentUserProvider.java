package com.anikesh.saas_backend.security;

import com.anikesh.saas_backend.Exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserProvider {
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new CustomException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        return userDetails.getUserId();
    }
}
