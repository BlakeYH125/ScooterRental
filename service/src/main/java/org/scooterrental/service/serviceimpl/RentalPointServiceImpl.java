package org.scooterrental.service.serviceimpl;

import org.scooterrental.model.enums.RentalPointType;
import org.scooterrental.model.exception.InvalidHierarchyException;
import org.scooterrental.model.exception.RentalPointNotEmptyException;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.scooterrental.service.dto.RentalPointDetailsDto;
import org.scooterrental.service.dto.ScooterResponseDto;
import org.scooterrental.service.mapper.ScooterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.exception.RentalPointNotFoundException;
import org.scooterrental.model.exception.SameRentalPointsIDException;
import org.scooterrental.repository.daointerface.RentalPointDao;
import org.scooterrental.service.dto.RentalPointCreateDto;
import org.scooterrental.service.dto.RentalPointResponseDto;
import org.scooterrental.service.mapper.RentalPointMapper;
import org.scooterrental.service.serviceinterface.RentalPointService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RentalPointServiceImpl implements RentalPointService {
    private final RentalPointDao rentalPointDao;
    private final ScooterDao scooterDao;
    private final RentalPointMapper rentalPointMapper;
    private final ScooterMapper scooterMapper;
    private static final Logger logger = LoggerFactory.getLogger(RentalPointServiceImpl.class);

    public RentalPointServiceImpl(RentalPointDao rentalPointDao, RentalPointMapper rentalPointMapper, ScooterDao scooterDao, ScooterMapper scooterMapper) {
        this.rentalPointDao = rentalPointDao;
        this.rentalPointMapper = rentalPointMapper;
        this.scooterDao = scooterDao;
        this.scooterMapper = scooterMapper;
    }

    @Override
    public RentalPointResponseDto addNewRentalPoint(RentalPointCreateDto rentalPointCreateDto) {
        validateHierarchy(rentalPointCreateDto.getRentalPointType(), rentalPointCreateDto.getParentPointId());
        RentalPoint parentPoint = null;
        Long parentPointId = rentalPointCreateDto.getParentPointId();
        if (parentPointId != null) {
            parentPoint = getRentalPointOrThrow(parentPointId);
        }
        RentalPoint rentalPoint = rentalPointMapper.toRentalPointEntity(rentalPointCreateDto, parentPoint);
        rentalPointDao.create(rentalPoint);
        logger.info("Новая точка аренды с местоположением {} успешно добавлена", rentalPointCreateDto.getLocation());
        return rentalPointMapper.toRentalPointDto(rentalPoint);
    }

    @Override
    public RentalPointResponseDto setNewRentalPointLocation(Long rentalPointId, String newLocation) {
        RentalPoint rentalPoint = getRentalPointOrThrow(rentalPointId);
        rentalPoint.setLocation(newLocation);
        rentalPointDao.update(rentalPoint);
        logger.info("Точке аренды {} успешно установлена новая локация", rentalPointId);
        return rentalPointMapper.toRentalPointDto(rentalPoint);
    }

    @Override
    public RentalPointResponseDto setNewParentPointId(Long rentalPointId, Long newParentPointId) {
        RentalPoint newParentPoint = getRentalPointOrThrow(newParentPointId);
        RentalPoint rentalPoint = getRentalPointOrThrow(rentalPointId);
        validateHierarchy(rentalPoint.getRentalPointType(), newParentPointId);
        if (rentalPointId.equals(newParentPointId)) {
            throw new SameRentalPointsIDException();
        }
        rentalPoint.setParentPoint(newParentPoint);
        rentalPointDao.update(rentalPoint);
        logger.info("Точке аренды {} успешно установлена новая родительская точка", rentalPointId);
        return rentalPointMapper.toRentalPointDto(rentalPoint);
    }

    @Override
    public void deleteRentalPoint(Long rentalPointId) {
        RentalPoint rentalPoint = getRentalPointOrThrow(rentalPointId);
        if (scooterDao.countScootersAtRentalPoint(rentalPointId) > 0) {
            throw new RentalPointNotEmptyException();
        }
        if (!rentalPointDao.delete(rentalPointId)) {
            throw new RuntimeException("Ошибка при удалении");
        }
        logger.info("Точке аренды {} успешно удалена", rentalPointId);
    }

    @Override
    public RentalPointResponseDto getRentalPoint(Long rentalPointId) {
        RentalPoint rentalPoint = getRentalPointOrThrow(rentalPointId);
        logger.info("Точке аренды {} успешно запрошена", rentalPointId);
        return rentalPointMapper.toRentalPointDto(rentalPoint);
    }

    @Override
    public List<RentalPointResponseDto> getAllRentalPoints() {
        List<RentalPointResponseDto> list = rentalPointDao.findRentalPoints().stream()
                .map(rentalPointMapper::toRentalPointDto)
                .toList();
        logger.info("Успешно запрошен список всех точек аренды");
        return list;
    }

    @Override
    public RentalPointDetailsDto getRentalPointDetails(Long rentalPointId) {
        RentalPoint rentalPoint = getRentalPointOrThrow(rentalPointId);
        List<ScooterResponseDto> scooters = scooterDao.findScootersByRentalPoint(rentalPointId).stream()
                .map(scooterMapper::toScooterDto)
                .toList();
        RentalPointDetailsDto rentalPointDetailsDto = new RentalPointDetailsDto();
        rentalPointDetailsDto.setRentalPointId(rentalPointId);
        rentalPointDetailsDto.setLocation(rentalPoint.getLocation());
        rentalPointDetailsDto.setScooters(scooters);
        rentalPointDetailsDto.setDeleted(rentalPoint.isDeleted());
        rentalPointDetailsDto.setRentalPointType(rentalPoint.getRentalPointType().name());
        if (rentalPoint.getChildPoints() != null) {
            List<RentalPointResponseDto> children = rentalPoint.getChildPoints().stream()
                    .map(rentalPointMapper::toRentalPointDto)
                    .toList();
            rentalPointDetailsDto.setChildPoints(children);
        }
        logger.info("Точка аренды {} со всеми деталями успешно запрошена", rentalPointId);
        return rentalPointDetailsDto;
    }

    @Override
    public List<RentalPointResponseDto> getRentalStationsByParentId(Long parentPointId) {
        RentalPoint startPoint = getRentalPointOrThrow(parentPointId);
        List<RentalPoint> result = new ArrayList<>();
        extractBuildings(startPoint, result);
        logger.info("Информация о дочерних точках аренды {} успешно запрошена", parentPointId);
        return result.stream()
                .map(rentalPoint -> {
                    RentalPointResponseDto rentalPointResponseDto = rentalPointMapper.toRentalPointDto(rentalPoint);
                    if (rentalPoint.getParentPoint() != null) {
                        String fullLocation = rentalPoint.getParentPoint().getLocation() + ", " + rentalPoint.getLocation();
                        rentalPointResponseDto.setLocation(fullLocation);
                    }
                    return rentalPointResponseDto;
                })
                .toList();
    }

    private RentalPoint getRentalPointOrThrow(Long rentalPointId) {
        RentalPoint rentalPoint = rentalPointDao.findRentalPointById(rentalPointId);
        if (rentalPoint == null) {
            throw new RentalPointNotFoundException();
        }
        return rentalPoint;
    }

    private void validateHierarchy(RentalPointType newRentalPointType, Long parentPointId) {
        if (newRentalPointType == RentalPointType.CITY && parentPointId != null) {
            throw new InvalidHierarchyException("У города не может быть родительской точки");
        }
        if (newRentalPointType != RentalPointType.CITY && parentPointId == null) {
            throw new InvalidHierarchyException("У данной точки обязан быть родитель");
        }
        if (parentPointId == null) {
            return;
        }
        RentalPoint parentPoint = getRentalPointOrThrow(parentPointId);
        RentalPointType parentPointType = parentPoint.getRentalPointType();
        if (newRentalPointType == RentalPointType.DISTRICT && parentPointType != RentalPointType.CITY) {
            throw new InvalidHierarchyException("Неверная иерархия: район должен принадлежать городу");
        }
        if (newRentalPointType == RentalPointType.STREET && parentPointType != RentalPointType.DISTRICT) {
            throw new InvalidHierarchyException("Неверная иерархия: улица должна принадлежать району");
        }
        if (newRentalPointType == RentalPointType.BUILDING && parentPointType != RentalPointType.STREET) {
            throw new InvalidHierarchyException("Неверная иерархия: здание должно принадлежать улице");
        }
    }

    private void extractBuildings(RentalPoint currentPoint, List<RentalPoint> result) {
        if (currentPoint.isDeleted()) {
            return;
        }
        if (currentPoint.getRentalPointType() == RentalPointType.BUILDING) {
            result.add(currentPoint);
        } else {
            for (RentalPoint child : currentPoint.getChildPoints()) {
                extractBuildings(child, result);
            }
        }
    }
}
