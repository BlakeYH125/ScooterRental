package org.scooterrental.service.dto;

import java.util.List;

public class RentalPointDetailsDto {
    private Long rentalPointId;
    private String location;
    private List<ScooterResponseDto> scooters;

    public RentalPointDetailsDto() {
    }

    public Long getRentalPointId() {
        return rentalPointId;
    }

    public void setRentalPointId(Long rentalPointId) {
        this.rentalPointId = rentalPointId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<ScooterResponseDto> getScooters() {
        return scooters;
    }

    public void setScooters(List<ScooterResponseDto> scooters) {
        this.scooters = scooters;
    }
}
