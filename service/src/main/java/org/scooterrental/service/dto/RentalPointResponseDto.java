package org.scooterrental.service.dto;

public class RentalPointResponseDto {
    private Long rentalPointId;
    private String location;
    private Long parentPointId;
    private boolean deleted;

    public RentalPointResponseDto() {
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

    public Long getParentPointId() {
        return parentPointId;
    }

    public void setParentPointId(Long parentPointId) {
        this.parentPointId = parentPointId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
