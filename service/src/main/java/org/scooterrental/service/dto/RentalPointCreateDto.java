package org.scooterrental.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class RentalPointCreateDto {

    @NotBlank(message = "Локация обязательная")
    private String location;

    @Positive(message = "ID родительской точки должен быть больше 0")
    private Long parentPointId;

    public RentalPointCreateDto() {
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
