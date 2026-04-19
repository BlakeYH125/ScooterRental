package org.scooterrental.service.dto;

import java.util.List;

public class RentalPointDetailsDto {
    private Long rentalPointId;
    private String rentalPointType;
    private String location;
    private boolean deleted;
    private List<ScooterResponseDto> scooters;
    private List<RentalPointResponseDto> childPoints;

    public RentalPointDetailsDto() {
    }

    public Long getRentalPointId() {
        return rentalPointId;
    }

    public void setRentalPointId(Long rentalPointId) {
        this.rentalPointId = rentalPointId;
    }

    public String getRentalPointType() {
        return rentalPointType;
    }

    public void setRentalPointType(String rentalPointType) {
        this.rentalPointType = rentalPointType;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<RentalPointResponseDto> getChildPoints() {
        return childPoints;
    }

    public void setChildPoints(List<RentalPointResponseDto> childPoints) {
        this.childPoints = childPoints;
    }
}
