package com.anikesh.saas_backend.dto;

public class AuthResponseDTO {

    private String accessToken;
    private String tokenType;

    public AuthResponseDTO(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
