package com.anikesh.saas_backend.service;

import com.anikesh.saas_backend.entity.User;
import com.anikesh.saas_backend.repository.UserRepository;
import com.anikesh.saas_backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,AuthenticationManager authenticationManager,JwtUtil jwtUtil){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.authenticationManager=authenticationManager;
        this.jwtUtil=jwtUtil;
    }

    public String register(String name , String email, String password){
         
        if(userRepository.findByEmail(email).isPresent()){
            throw new RuntimeException("Email already registered");
        }

        User user=new User();

        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setIsActive(true);

        userRepository.save(user);

        return jwtUtil.generateToken(user.getUserId(),user.getEmail());
    }

    public String login(String email, String password){
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        User user=userRepository.findByEmail(email).orElseThrow();

        return jwtUtil.generateToken(
                user.getUserId(),
                user.getEmail()
        );
    }
}
