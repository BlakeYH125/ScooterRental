package org.scooterrental.service.serviceinterface;

import org.scooterrental.model.enums.ScooterStatus;
import org.scooterrental.service.dto.ScooterCreateDto;
import org.scooterrental.service.dto.ScooterResponseDto;

import java.util.List;

public interface ScooterService {
    ScooterResponseDto addNewScooter(ScooterCreateDto scooterCreateDto);
    ScooterResponseDto setNewScooterModel(Long scooterId, String newScooterModel);
    ScooterResponseDto setNewBatteryLevel(Long scooterId, int newBatteryLevel);
    ScooterResponseDto setNewScooterStatus(Long scooterId, ScooterStatus newScooterStatus);
    void deleteScooter(Long scooterId);
    ScooterResponseDto getScooter(Long scooterId);
    List<ScooterResponseDto> getAllScooters();
}
