package org.scooterrental.service.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthenticationResponseDto {

    @NotBlank
    private String token;

    public AuthenticationResponseDto() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
