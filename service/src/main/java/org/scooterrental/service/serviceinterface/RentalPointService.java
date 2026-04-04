package org.scooterrental.service.serviceinterface;

import org.scooterrental.service.dto.RentalPointCreateDto;
import org.scooterrental.service.dto.RentalPointResponseDto;

import java.util.List;

public interface RentalPointService {
    RentalPointResponseDto addNewRentalPoint(RentalPointCreateDto rentalPointCreateDto);
    RentalPointResponseDto setNewRentalPointLocation(Long rentalPointId, String newLocation);
    RentalPointResponseDto setNewParentPointId(Long rentalPointId, Long newParentPointId);
    void deleteRentalPoint(Long rentalPointId);
    RentalPointResponseDto getRentalPoint(Long rentalPointId);
    List<RentalPointResponseDto> getAllRentalPoints();
}
