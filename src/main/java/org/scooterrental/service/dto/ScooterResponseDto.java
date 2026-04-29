package org.scooterrental.service.dto;

import lombok.Data;

@Data
public class ScooterResponseDto {
    private Long scooterId;

    private String model;

    private int batteryLevel;

    private String scooterStatus;

    private Long rentalPointId;

    private double mileage;
}
