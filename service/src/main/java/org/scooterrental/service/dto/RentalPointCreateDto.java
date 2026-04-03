package org.scooterrental.service.dto;

public class RentalPointCreateDto {
    private String location;
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
