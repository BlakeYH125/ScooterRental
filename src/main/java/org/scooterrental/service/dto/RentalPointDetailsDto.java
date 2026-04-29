package org.scooterrental.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class RentalPointDetailsDto {
    private Long rentalPointId;
    private String rentalPointType;
    private String location;
    private boolean deleted;
    private List<ScooterResponseDto> scooters;
    private List<RentalPointResponseDto> childPoints;
}
