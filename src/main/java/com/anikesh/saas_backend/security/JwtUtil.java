package com.anikesh.saas_backend.security;

import javax.crypto.SecretKey;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

 @Component
public class JwtUtil {
    
    private final SecretKey key;
    private final long expirationMs;
    
    public JwtUtil(@Value("${jwt.secret}") String secret,@Value("${jwt.expiration-ms}") long expirationMs){
                    this.key= Keys.hmacShaKeyFor(secret.getBytes());
                    this.expirationMs=expirationMs;
                   }

    public String generateToken(long userId, String email){
        Date now=new Date();
        Date expiry=new Date(now.getTime()+expirationMs);
        
        return Jwts.builder().subject(String.valueOf(userId)).claim("email",email).issuedAt(now).expiration(expiry).signWith(key).compact();
    }

    public Claims extractClaims(String token){

        return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
    }

    public Long extractUserId(String token){
        return Long.valueOf(extractClaims(token).getSubject());
    }

    public boolean isTokenValid(String token){
        try{
            extractClaims(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
