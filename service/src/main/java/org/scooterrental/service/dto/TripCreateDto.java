package org.scooterrental.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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

    public TripCreateDto() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getScooterId() {
        return scooterId;
    }

    public void setScooterId(Long scooterId) {
        this.scooterId = scooterId;
    }

    public Long getTariffId() {
        return tariffId;
    }

    public void setTariffId(Long tariffId) {
        this.tariffId = tariffId;
    }
}
