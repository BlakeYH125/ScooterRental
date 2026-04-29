package org.scooterrental.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.scooterrental.model.enums.RentalPointType;

@Data
public class RentalPointCreateDto {

    @NotNull(message = "Тип точки аренды обязателен")
    private RentalPointType rentalPointType;

    @NotBlank(message = "Локация обязательная")
    private String location;

    @Positive(message = "ID родительской точки должен быть больше 0")
    private Long parentPointId;
}
