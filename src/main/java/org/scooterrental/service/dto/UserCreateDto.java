package org.scooterrental.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UserCreateDto {

    @NotBlank(message = "Имя пользователя обязательно")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    private String password;

    @NotBlank(message = "Имя обязательно")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    @Positive(message = "Возраст должен быть положительным")
    private int age;
}
