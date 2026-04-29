package org.scooterrental.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {
    @NotBlank(message = "Старый пароль обязателен")
    private String oldPassword;

    @NotBlank(message = "Новый пароль обязателен")
    @Size(min = 8)
    private String newPassword;
}
