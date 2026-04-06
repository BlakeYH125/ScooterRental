package org.scooterrental.service.serviceimpl;

import org.scooterrental.model.entity.Scooter;
import org.scooterrental.model.enums.ScooterStatus;
import org.scooterrental.model.exception.ScooterAlreadyInRentException;
import org.scooterrental.model.exception.ScooterNotFoundException;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.scooterrental.service.dto.ScooterCreateDto;
import org.scooterrental.service.dto.ScooterResponseDto;
import org.scooterrental.service.mapper.ScooterMapper;
import org.scooterrental.service.serviceinterface.ScooterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ScooterServiceImpl implements ScooterService {
    private final ScooterDao scooterDao;
    private final ScooterMapper scooterMapper;

    public ScooterServiceImpl(ScooterDao scooterDao, ScooterMapper scooterMapper) {
        this.scooterDao = scooterDao;
        this.scooterMapper = scooterMapper;
    }

    @Override
    public ScooterResponseDto addNewScooter(ScooterCreateDto scooterCreateDto) {
        Scooter scooter = scooterMapper.toScooterEntity(scooterCreateDto);
        scooterDao.create(scooter);
        return scooterMapper.toScooterDto(scooter);
    }

    @Override
    public ScooterResponseDto setNewScooterModel(Long scooterId, String newScooterModel) {
        Scooter scooter = getScooterOrThrow(scooterId);
        scooter.setModel(newScooterModel);
        scooterDao.update(scooter);
        return scooterMapper.toScooterDto(scooter);
    }

    @Override
    public ScooterResponseDto setNewBatteryLevel(Long scooterId, int newBatteryLevel) {
        Scooter scooter = getScooterOrThrow(scooterId);
        if (newBatteryLevel < 0 || newBatteryLevel > 100) {
            throw new IllegalArgumentException("Процент заряда не может выходить за диапазон 0-100");
        }
        scooter.setBatteryLevel(newBatteryLevel);
        scooterDao.update(scooter);
        return scooterMapper.toScooterDto(scooter);
    }

    @Override
    public ScooterResponseDto rechargeBattery(Long scooterId) {
        Scooter scooter = getScooterOrThrow(scooterId);
        scooter.setBatteryLevel(100);
        scooterDao.update(scooter);
        return scooterMapper.toScooterDto(scooter);
    }

    @Override
    public ScooterResponseDto setNewScooterStatus(Long scooterId, ScooterStatus newScooterStatus) {
        Scooter scooter = getScooterOrThrow(scooterId);
        if (scooter.getScooterStatus() == ScooterStatus.IN_RENT && newScooterStatus == ScooterStatus.IN_WAREHOUSE) {
            throw new ScooterAlreadyInRentException();
        }
        scooter.setScooterStatus(newScooterStatus);
        if (newScooterStatus == ScooterStatus.IN_WAREHOUSE) {
            scooter.setRentalPoint(null);
        }
        scooterDao.update(scooter);
        return scooterMapper.toScooterDto(scooter);
    }

    @Override
    public void deleteScooter(Long scooterId) {
        if (!scooterDao.delete(scooterId)) {
            throw new IllegalArgumentException("Ошибка при удалении");
        }
    }

    @Override
    public ScooterResponseDto getScooter(Long scooterId) {
        Scooter scooter = getScooterOrThrow(scooterId);
        return scooterMapper.toScooterDto(scooter);
    }

    @Override
    public List<ScooterResponseDto> getAllScooters() {
        return scooterDao.findScooters().stream()
                .map(scooterMapper::toScooterDto)
                .toList();
    }

    private Scooter getScooterOrThrow(Long scooterId) {
        Scooter scooter = scooterDao.findScooter(scooterId);
        if (scooter == null) {
            throw new ScooterNotFoundException();
        }
        return scooter;
    }
}
