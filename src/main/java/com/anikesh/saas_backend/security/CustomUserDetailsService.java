package com.anikesh.saas_backend.security;

import com.anikesh.saas_backend.entity.*;
import com.anikesh.saas_backend.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    public final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository= userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("No user with email: "+ email));

        return new CustomUserDetails(user);
    }
}
