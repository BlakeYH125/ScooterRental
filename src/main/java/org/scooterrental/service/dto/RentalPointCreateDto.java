package org.scooterrental.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.scooterrental.model.enums.RentalPointType;

public class RentalPointCreateDto {

    @NotNull(message = "Тип точки аренды обязателен")
    private RentalPointType rentalPointType;

    @NotBlank(message = "Локация обязательная")
    private String location;

    @Positive(message = "ID родительской точки должен быть больше 0")
    private Long parentPointId;

    public RentalPointCreateDto() {
    }

    public RentalPointType getRentalPointType() {
        return rentalPointType;
    }

    public void setRentalPointType(RentalPointType rentalPointType) {
        this.rentalPointType = rentalPointType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getParentPointId() {
        return parentPointId;
    }

    public void setParentPointId(Long parentPointId) {
        this.parentPointId = parentPointId;
    }
}
