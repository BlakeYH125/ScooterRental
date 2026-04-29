package org.scooterrental.service.dto;

import lombok.Data;

@Data
public class RentalPointResponseDto {
    private Long rentalPointId;
    private String rentalPointType;
    private String location;
    private Long parentPointId;
    private boolean deleted;
}
