package org.scooterrental.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationResponseDto {

    @NotBlank
    private String token;
}
