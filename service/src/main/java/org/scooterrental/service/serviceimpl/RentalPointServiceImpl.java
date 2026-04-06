package org.scooterrental.service.serviceimpl;

import org.scooterrental.model.exception.RentalPointNotEmptyException;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.springframework.transaction.annotation.Transactional;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.exception.RentalPointAlreadyExistsException;
import org.scooterrental.model.exception.RentalPointNotFoundException;
import org.scooterrental.model.exception.SameRentalPointsIDException;
import org.scooterrental.repository.daointerface.RentalPointDao;
import org.scooterrental.service.dto.RentalPointCreateDto;
import org.scooterrental.service.dto.RentalPointResponseDto;
import org.scooterrental.service.mapper.RentalPointMapper;
import org.scooterrental.service.serviceinterface.RentalPointService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RentalPointServiceImpl implements RentalPointService {
    private final RentalPointDao rentalPointDao;
    private final ScooterDao scooterDao;
    private final RentalPointMapper rentalPointMapper;

    public RentalPointServiceImpl(RentalPointDao rentalPointDao, RentalPointMapper rentalPointMapper, ScooterDao scooterDao) {
        this.rentalPointDao = rentalPointDao;
        this.rentalPointMapper = rentalPointMapper;
        this.scooterDao = scooterDao;
    }

    @Override
    public RentalPointResponseDto addNewRentalPoint(RentalPointCreateDto rentalPointCreateDto) {
        String location = rentalPointCreateDto.getLocation();
        RentalPoint rentalPoint = rentalPointDao.findRentalPointByLocation(location);
        if (rentalPoint != null) {
            throw new RentalPointAlreadyExistsException();
        }
        RentalPoint parentPoint = null;
        Long parentPointId = rentalPointCreateDto.getParentPointId();
        if (parentPointId != null) {
            parentPoint = rentalPointDao.findRentalPointById(parentPointId);
            if (parentPoint == null) {
                throw new RentalPointNotFoundException();
            }
        }
        rentalPoint = rentalPointMapper.toRentalPointEntity(rentalPointCreateDto, parentPoint);
        rentalPointDao.create(rentalPoint);
        return rentalPointMapper.toRentalPointDto(rentalPoint);
    }

    @Override
    public RentalPointResponseDto setNewRentalPointLocation(Long rentalPointId, String newLocation) {
        RentalPoint rentalPoint = rentalPointDao.findRentalPointByLocation(newLocation);
        if (rentalPoint != null) {
            throw new RentalPointAlreadyExistsException();
        }
        rentalPoint = rentalPointDao.findRentalPointById(rentalPointId);
        if (rentalPoint == null) {
            throw new RentalPointNotFoundException();
        }
        rentalPoint.setLocation(newLocation);
        rentalPointDao.update(rentalPoint);
        return rentalPointMapper.toRentalPointDto(rentalPoint);
    }

    @Override
    public RentalPointResponseDto setNewParentPointId(Long rentalPointId, Long newParentPointId) {
        RentalPoint newParentPoint = rentalPointDao.findRentalPointById(newParentPointId);
        if (newParentPoint == null) {
            throw new RentalPointNotFoundException();
        }
        RentalPoint rentalPoint = rentalPointDao.findRentalPointById(rentalPointId);
        if (rentalPoint == null) {
            throw new RentalPointNotFoundException();
        }
        if (rentalPointId.equals(newParentPointId)) {
            throw new SameRentalPointsIDException();
        }
        rentalPoint.setParentPoint(newParentPoint);
        rentalPointDao.update(rentalPoint);
        return rentalPointMapper.toRentalPointDto(rentalPoint);
    }

    @Override
    public void deleteRentalPoint(Long rentalPointId) {
        RentalPoint rentalPoint = rentalPointDao.findRentalPointById(rentalPointId);
        if (rentalPoint == null) {
            throw new RentalPointNotFoundException();
        }
        if (scooterDao.countScootersAtRentalPoint(rentalPointId) > 0) {
            throw new RentalPointNotEmptyException();
        }
        if (!rentalPointDao.delete(rentalPointId)) {
            throw new IllegalArgumentException("Ошибка при удалении");
        }
    }

    @Override
    public RentalPointResponseDto getRentalPoint(Long rentalPointId) {
        RentalPoint rentalPoint = rentalPointDao.findRentalPointById(rentalPointId);
        if (rentalPoint == null) {
            throw new RentalPointNotFoundException();
        }
        return rentalPointMapper.toRentalPointDto(rentalPoint);
    }

    @Override
    public List<RentalPointResponseDto> getAllRentalPoints() {
        return rentalPointDao.findRentalPoints().stream()
                .map(rentalPointMapper::toRentalPointDto)
                .toList();
    }
}
