package org.scooterrental.service.dto;

import jakarta.validation.constraints.NotBlank;

public class ScooterCreateDto {

    @NotBlank(message = "Название модели обязательно")
    private String model;

    public ScooterCreateDto() {
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
