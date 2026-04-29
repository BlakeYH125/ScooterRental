package org.scooterrental.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScooterCreateDto {

    @NotBlank(message = "Название модели обязательно")
    private String model;
}
