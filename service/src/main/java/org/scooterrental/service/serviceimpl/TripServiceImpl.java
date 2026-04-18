package org.scooterrental.service.serviceimpl;

import org.scooterrental.model.entity.Trip;
import org.scooterrental.model.entity.Tariff;
import org.scooterrental.model.entity.Scooter;
import org.scooterrental.model.entity.User;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.model.enums.PaymentType;
import org.scooterrental.model.enums.ScooterStatus;
import org.scooterrental.model.enums.TripStatus;
import org.scooterrental.model.exception.RentalPointNotFoundException;
import org.scooterrental.model.exception.ScooterNotFoundException;
import org.scooterrental.model.exception.ScooterNotAvailableException;
import org.scooterrental.model.exception.UserBannedException;
import org.scooterrental.model.exception.UserAlreadyHasActiveTripException;
import org.scooterrental.model.exception.UserHasNoActiveSeasonTicketException;
import org.scooterrental.model.exception.UserNotFoundException;
import org.scooterrental.model.exception.TariffNotFoundException;
import org.scooterrental.model.exception.TripAlreadyCompletedException;
import org.scooterrental.model.exception.TripNotFoundException;
import org.scooterrental.model.exception.ValueLessZeroException;
import org.scooterrental.model.exception.LowBatteryLevelException;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.scooterrental.repository.daointerface.UserDao;
import org.scooterrental.repository.daointerface.RentalPointDao;
import org.scooterrental.repository.daointerface.TripDao;
import org.scooterrental.repository.daointerface.TariffDao;
import org.scooterrental.service.dto.TripCreateDto;
import org.scooterrental.service.dto.TripResponseDto;
import org.scooterrental.service.mapper.TripMapper;
import org.scooterrental.service.serviceinterface.TripService;
import org.scooterrental.service.serviceinterface.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TripServiceImpl implements TripService {
    private final TripDao tripDao;
    private final UserDao userDao;
    private final ScooterDao scooterDao;
    private final TariffDao tariffDao;
    private final RentalPointDao rentalPointDao;
    private final TripMapper tripMapper;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);

    public TripServiceImpl(TripDao tripDao, UserDao userDao, ScooterDao scooterDao, TariffDao tariffDao, RentalPointDao rentalPointDao, TripMapper tripMapper, UserService userService) {
        this.tripDao = tripDao;
        this.userDao = userDao;
        this.scooterDao = scooterDao;
        this.tariffDao = tariffDao;
        this.rentalPointDao = rentalPointDao;
        this.tripMapper = tripMapper;
        this.userService = userService;
    }

    @Override
    public TripResponseDto startTrip(TripCreateDto tripCreateDto) {
        Long userId = tripCreateDto.getUserId();
        Long scooterId = tripCreateDto.getScooterId();
        Long tariffId = tripCreateDto.getTariffId();
        User user = userDao.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (user.getBanReason() != BanReason.NONE) {
            throw new UserBannedException();
        }
        if (user.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValueLessZeroException("Невозможно начать поездку при отрицательном балансе");
        }
        Scooter scooter = scooterDao.findScooter(scooterId);
        if (scooter == null) {
            throw new ScooterNotFoundException();
        }
        if (scooter.getScooterStatus() != ScooterStatus.AVAILABLE) {
            throw new ScooterNotAvailableException();
        }
        if (scooter.getBatteryLevel() < 10) {
            throw new LowBatteryLevelException();
        }
        Tariff tariff = tariffDao.findTariff(tariffId);
        if (tariff == null) {
            throw new TariffNotFoundException();
        }
        if (tariff.getPaymentType() == PaymentType.SEASON_TICKET && user.getSeasonTicketEndDate() == null) {
            throw new UserHasNoActiveSeasonTicketException();
        }
        if (tripDao.isThereActiveTripByUserId(userId)) {
            throw new UserAlreadyHasActiveTripException();
        }
        scooter.setScooterStatus(ScooterStatus.IN_RENT);
        Trip trip = new Trip(user, scooter, scooter.getRentalPoint(), LocalDateTime.now(), tariff);
        scooter.setRentalPoint(null);
        tripDao.create(trip);
        scooterDao.update(scooter);
        logger.info("Пользователь с id {} успешно начал поездку на самокате {}", userId, scooterId);
        return tripMapper.toTripDto(trip);
    }

    @Override
    public TripResponseDto finishTrip(Long tripId, Long endRentalPointId, Long userId) {
        Trip trip = getTripOrThrow(tripId);
        if (!trip.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Вы не можете завершить чужую поездку");
        }
        if (trip.getTripStatus() != TripStatus.ACTIVE) {
            throw new TripAlreadyCompletedException();
        }
        RentalPoint endRentalPoint = rentalPointDao.findRentalPointById(endRentalPointId);
        if (endRentalPoint == null) {
            throw new RentalPointNotFoundException();
        }
        User user = trip.getUser();
        Tariff tariff = trip.getTariff();
        Scooter scooter = trip.getScooter();
        LocalDateTime endTime = LocalDateTime.now();
        long tripTimeMinutes = Duration.between(trip.getStartTime(), endTime).toMinutes();
        long tripTimeHours = (long) Math.ceil((tripTimeMinutes) / 60.0);
        if (tripTimeHours == 0) {
            tripTimeHours = 1;
        }
        if (tripTimeMinutes == 0) {
            tripTimeMinutes = 1;
        }
        BigDecimal totalCost = BigDecimal.ZERO;
        if (tariff.getPaymentType() == PaymentType.HOURLY) {
            totalCost = tariff.getPrice().multiply(BigDecimal.valueOf(tripTimeHours)).multiply(BigDecimal.valueOf((100 - tariff.getDiscount()) / 100.0));
        }
        trip.setTripStatus(TripStatus.COMPLETED);
        trip.setEndPoint(endRentalPoint);
        trip.setEndTime(endTime);
        trip.setMileage(tripTimeMinutes * 0.25);
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);
        scooter.setRentalPoint(endRentalPoint);
        scooter.setMileage(scooter.getMileage() + tripTimeMinutes * 0.25);
        userService.debitMoney(user.getUserId(), totalCost);
        trip.setTotalCost(totalCost);
        tripDao.update(trip);
        scooterDao.update(scooter);
        logger.info("Пользователь с id {} успешно завершил поездку {} на самокате {}", userId, tripId, scooter.getScooterId());
        return tripMapper.toTripDto(trip);
    }

    @Override
    public TripResponseDto emergencyFinishTrip(Long tripId) {
        Trip trip = getTripOrThrow(tripId);
        if (trip.getTripStatus() != TripStatus.ACTIVE) {
            throw new TripAlreadyCompletedException();
        }
        User user = trip.getUser();
        Tariff tariff = trip.getTariff();
        Scooter scooter = trip.getScooter();
        LocalDateTime endTime = LocalDateTime.now();
        long tripTimeMinutes = Duration.between(trip.getStartTime(), endTime).toMinutes();
        long tripTimeHours = (long) Math.ceil((tripTimeMinutes) / 60.0);
        if (tripTimeHours == 0) {
            tripTimeHours = 1;
        }
        if (tripTimeMinutes == 0) {
            tripTimeMinutes = 1;
        }
        BigDecimal totalCost = BigDecimal.ZERO;
        if (tariff.getPaymentType() == PaymentType.HOURLY) {
            totalCost = tariff.getPrice().multiply(BigDecimal.valueOf(tripTimeHours)).multiply(BigDecimal.valueOf(((100 - tariff.getDiscount()) / 100.0)));
        }
        trip.setTripStatus(TripStatus.COMPLETED);
        trip.setEndTime(endTime);
        trip.setMileage(tripTimeMinutes * 0.25);
        scooter.setScooterStatus(ScooterStatus.IN_SERVICE);
        scooter.setMileage(scooter.getMileage() + (tripTimeMinutes * 0.25));
        userService.debitMoney(user.getUserId(), totalCost);
        trip.setTotalCost(totalCost);
        tripDao.update(trip);
        scooterDao.update(scooter);
        logger.info("Поездка {} на самокате {} аварийно завершена", tripId, scooter.getScooterId());
        return tripMapper.toTripDto(trip);
    }

    @Override
    public TripResponseDto getTrip(Long tripId) {
        Trip trip = getTripOrThrow(tripId);
        logger.info("Поездка {} успешно запрошена", tripId);
        return tripMapper.toTripDto(trip);
    }

    @Override
    public List<TripResponseDto> getAllTrips() {
        List<TripResponseDto> list =tripDao.findTrips().stream()
                .map(tripMapper::toTripDto)
                .toList();
        logger.info("Успешно запрошен список всех поездок системы");
        return list;

    }

    @Override
    public List<TripResponseDto> getUserHistory(Long userId) {
        User user = userDao.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        List<TripResponseDto> list = tripDao.findTripsByUserId(userId).stream()
                .map(tripMapper::toTripDto)
                .toList();
        logger.info("Пользователь {} успешно запросил историю своих поездок", userId);
        return list;
    }

    @Override
    public List<TripResponseDto> getScooterHistory(Long scooterId) {
        Scooter scooter = scooterDao.findScooter(scooterId);
        if (scooter == null) {
            throw new ScooterNotFoundException();
        }
        List<TripResponseDto> list = tripDao.findTripsByScooterId(scooterId).stream()
                .map(tripMapper::toTripDto)
                .toList();
        logger.info("Успешно запрошена история самоката {}", scooterId);
        return list;
    }

    private Trip getTripOrThrow(Long tripId) {
        Trip trip = tripDao.findTrip(tripId);
        if (trip == null) {
            throw new TripNotFoundException();
        }
        return trip;
    }
}
