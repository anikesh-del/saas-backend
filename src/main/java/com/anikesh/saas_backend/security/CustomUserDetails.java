package com.anikesh.saas_backend.security;
import com.anikesh.saas_backend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    
    private final User user;
    
    public CustomUserDetails(User user){
        this.user=user;
    }

    public Long getUserId(){
        return user.getUserId();
    }
    
    @Override
    public String getUsername(){
        return user.getEmail();
    }
    
    @Override
    public String getPassword(){
        return user.getHashedPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of();
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
}
