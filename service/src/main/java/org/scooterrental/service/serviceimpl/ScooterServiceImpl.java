package org.scooterrental.service.serviceimpl;

import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.entity.Scooter;
import org.scooterrental.model.enums.ScooterStatus;
import org.scooterrental.model.exception.*;
import org.scooterrental.repository.daointerface.RentalPointDao;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.scooterrental.service.dto.ScooterCreateDto;
import org.scooterrental.service.dto.ScooterResponseDto;
import org.scooterrental.service.mapper.ScooterMapper;
import org.scooterrental.service.serviceinterface.ScooterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ScooterServiceImpl implements ScooterService {
    private final ScooterDao scooterDao;
    private final RentalPointDao rentalPointDao;
    private final ScooterMapper scooterMapper;
    private static final Logger logger = LoggerFactory.getLogger(ScooterServiceImpl.class);

    public ScooterServiceImpl(ScooterDao scooterDao, RentalPointDao rentalPointDao, ScooterMapper scooterMapper) {
        this.scooterDao = scooterDao;
        this.rentalPointDao = rentalPointDao;
        this.scooterMapper = scooterMapper;
    }

    @Override
    public ScooterResponseDto addNewScooter(ScooterCreateDto scooterCreateDto) {
        Scooter scooter = scooterMapper.toScooterEntity(scooterCreateDto);
        scooterDao.create(scooter);
        logger.info("Успешно добавлен новый самокат");
        return scooterMapper.toScooterDto(scooter);
    }

    @Override
    public ScooterResponseDto setNewScooterModel(Long scooterId, String newScooterModel) {
        Scooter scooter = getScooterOrThrow(scooterId);
        scooter.setModel(newScooterModel);
        scooterDao.update(scooter);
        logger.info("Самокату {} успешно установлена новая модель", scooterId);
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
        logger.info("Самокату {} успешно установлен новый процент заряда батареи", scooterId);
        return scooterMapper.toScooterDto(scooter);
    }

    @Override
    public ScooterResponseDto rechargeBattery(Long scooterId) {
        Scooter scooter = getScooterOrThrow(scooterId);
        scooter.setBatteryLevel(100);
        scooterDao.update(scooter);
        logger.info("Самокат {} успешно перезаряжен", scooterId);
        return scooterMapper.toScooterDto(scooter);
    }

    @Override
    public ScooterResponseDto putScooterInUse(Long scooterId, Long rentalPointId) {
        Scooter scooter = getScooterOrThrow(scooterId);
        if (scooter.getBatteryLevel() <= 5) {
            throw new LowBatteryLevelException();
        }
        if (scooter.getScooterStatus() == ScooterStatus.IN_RENT) {
            throw new ScooterAlreadyInRentException();
        }
        RentalPoint rentalPoint = rentalPointDao.findRentalPointById(rentalPointId);
        if (rentalPoint == null) {
            throw new RentalPointNotFoundException();
        }
        scooter.setRentalPoint(rentalPoint);
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);
        scooterDao.update(scooter);
        logger.info("Самокат {} успешно выпущен в пользование на точку аренды {}", scooterId, rentalPointId);
        return scooterMapper.toScooterDto(scooter);
    }

    @Override
    public ScooterResponseDto putScooterInWarehouse(Long scooterId) {
        Scooter scooter = getScooterOrThrow(scooterId);
        if (scooter.getScooterStatus() == ScooterStatus.IN_WAREHOUSE) {
            throw new ScooterInWarehouseException();
        }
        if (scooter.getScooterStatus() == ScooterStatus.IN_RENT) {
            throw new ScooterAlreadyInRentException();
        }
        scooter.setRentalPoint(null);
        scooter.setScooterStatus(ScooterStatus.IN_WAREHOUSE);
        scooterDao.update(scooter);
        logger.info("Самокат {} успешно отправлен на склад", scooterId);
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
        logger.info("Самокату {} успешно установлен новый статус", scooterId);
        return scooterMapper.toScooterDto(scooter);
    }

    @Override
    public void deleteScooter(Long scooterId) {
        if (!scooterDao.delete(scooterId)) {
            throw new IllegalArgumentException("Ошибка при удалении");
        }
        logger.info("Самокат {} успешно удален", scooterId);
    }

    @Override
    public ScooterResponseDto getScooter(Long scooterId) {
        Scooter scooter = getScooterOrThrow(scooterId);
        logger.info("Самокат {} успешно запрошен", scooterId);
        return scooterMapper.toScooterDto(scooter);
    }

    @Override
    public List<ScooterResponseDto> getAllScooters() {
        List<ScooterResponseDto> list = scooterDao.findScooters().stream()
                .map(scooterMapper::toScooterDto)
                .toList();
        logger.info("Успешно запрошен список всех самокатов");
        return list;
    }

    private Scooter getScooterOrThrow(Long scooterId) {
        Scooter scooter = scooterDao.findScooter(scooterId);
        if (scooter == null) {
            throw new ScooterNotFoundException();
        }
        return scooter;
    }
}
