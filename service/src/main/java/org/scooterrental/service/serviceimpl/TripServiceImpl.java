package org.scooterrental.service.serviceimpl;

import org.scooterrental.model.entity.*;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.model.enums.PaymentType;
import org.scooterrental.model.enums.ScooterStatus;
import org.scooterrental.model.enums.TripStatus;
import org.scooterrental.model.exception.*;
import org.scooterrental.repository.daointerface.*;
import org.scooterrental.service.dto.TripResponseDto;
import org.scooterrental.service.mapper.TripMapper;
import org.scooterrental.service.serviceinterface.TripService;
import org.scooterrental.service.serviceinterface.UserService;
import org.springframework.cglib.core.Local;
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
    public TripResponseDto startTrip(Long userId, Long scooterId, Long tariffId) {
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
            throw new ValueLessZeroException("Слишком низкий заряд батареи");
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
        return tripMapper.toTripDto(trip);
    }

    @Override
    public TripResponseDto finishTrip(Long tripId, Long endRentalPointId) {
        Trip trip = getTripOrThrow(tripId);
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
        long tripTime = (long) Math.ceil((Duration.between(trip.getStartTime(), endTime).toMinutes() / 60.0));
        if (tripTime == 0) {
            tripTime = 1;
        }
        BigDecimal totalCost = BigDecimal.ZERO;
        if (tariff.getPaymentType() == PaymentType.HOURLY) {
            totalCost = tariff.getPrice().multiply(BigDecimal.valueOf(tripTime)).multiply(BigDecimal.valueOf((100 - tariff.getDiscount()) / 100.0));
        }
        trip.setTripStatus(TripStatus.COMPLETED);
        trip.setEndPoint(endRentalPoint);
        trip.setEndTime(endTime);
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);
        scooter.setRentalPoint(endRentalPoint);
        userService.debitMoney(user.getUserId(), totalCost);
        trip.setTotalCost(totalCost);
        tripDao.update(trip);
        scooterDao.update(scooter);
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
        long tripTime = (long) Math.ceil((Duration.between(trip.getStartTime(), endTime).toMinutes() / 60.0));
        if (tripTime == 0) {
            tripTime = 1;
        }
        BigDecimal totalCost = BigDecimal.ZERO;
        if (tariff.getPaymentType() == PaymentType.HOURLY) {
            totalCost = tariff.getPrice().multiply(BigDecimal.valueOf(tripTime)).multiply(BigDecimal.valueOf(((100 - tariff.getDiscount()) / 100.0)));
        }
        trip.setTripStatus(TripStatus.COMPLETED);
        trip.setEndTime(endTime);
        scooter.setScooterStatus(ScooterStatus.IN_SERVICE);
        userService.debitMoney(user.getUserId(), totalCost);
        trip.setTotalCost(totalCost);
        tripDao.update(trip);
        scooterDao.update(scooter);
        return tripMapper.toTripDto(trip);
    }

    @Override
    public TripResponseDto getTrip(Long tripId) {
        Trip trip = getTripOrThrow(tripId);
        return tripMapper.toTripDto(trip);
    }

    @Override
    public List<TripResponseDto> getAllTrips() {
        return tripDao.findTrips().stream()
                .map(tripMapper::toTripDto)
                .toList();
    }

    private Trip getTripOrThrow(Long tripId) {
        Trip trip = tripDao.findTrip(tripId);
        if (trip == null) {
            throw new TripNotFoundException();
        }
        return trip;
    }
}
