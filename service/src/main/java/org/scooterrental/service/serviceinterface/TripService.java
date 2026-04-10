package org.scooterrental.service.serviceinterface;

import org.scooterrental.service.dto.TripCreateDto;
import org.scooterrental.service.dto.TripResponseDto;

import java.util.List;

public interface TripService {
    TripResponseDto startTrip(TripCreateDto tripCreateDto);
    TripResponseDto finishTrip(Long tripId, Long endRentalPointId, Long userId);
    TripResponseDto emergencyFinishTrip(Long tripId);
    TripResponseDto getTrip(Long tripId);
    List<TripResponseDto> getAllTrips();
    List<TripResponseDto> getUserHistory(Long userId);
    List<TripResponseDto> getScooterHistory(Long scooterId);
}
