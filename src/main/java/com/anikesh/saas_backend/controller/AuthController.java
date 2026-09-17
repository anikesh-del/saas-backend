package com.anikesh.saas_backend.controller;

import com.anikesh.saas_backend.service.AuthService;
import com.anikesh.saas_backend.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService=authService;
    }
    
    @PostMapping("/api/v1/register")
    public ResponseEntity<AuthResponseDTO> register(
        @Valid @RequestBody RegisterRequestDTO req
    ){
        String name=req.getName();
        String password=req.getPassword();
        String email=req.getEmail();
        
        String token=authService.register(name,email,password);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<AuthResponseDTO> login(
        @Valid @RequestBody LoginRequestDTO req
    ){
        String password=req.getPassword();
        String email=req.getEmail();

        String token=authService.login(email,password);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
