package org.scooterrental.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TripCreateDto {

    @NotNull(message = "ID пользователя обязателен")
    @Positive(message = "ID пользователя должен быть больше нуля")
    private Long userId;

    @NotNull(message = "ID самоката обязателен")
    @Positive(message = "ID самоката должен быть больше нуля")
    private Long scooterId;

    @NotNull(message = "ID тарифа обязателен")
    @Positive(message = "ID тарифа должен быть больше нуля")
    private Long tariffId;
}
