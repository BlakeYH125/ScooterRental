package org.scooterrental.service.serviceinterface;

import org.scooterrental.service.dto.TripResponseDto;

import java.util.List;

public interface TripService {
    TripResponseDto startTrip(Long userId, Long scooterId, Long tariffId);
    TripResponseDto finishTrip(Long tripId, Long endRentalPointId);
    TripResponseDto emergencyFinishTrip(Long tripId);
    TripResponseDto getTrip(Long tripId);
    List<TripResponseDto> getAllTrips();
}
