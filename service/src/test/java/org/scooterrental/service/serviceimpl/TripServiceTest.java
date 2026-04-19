package org.scooterrental.service.serviceimpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.entity.Scooter;
import org.scooterrental.model.entity.Tariff;
import org.scooterrental.model.entity.Trip;
import org.scooterrental.model.entity.User;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.model.enums.PaymentType;
import org.scooterrental.model.enums.RentalPointType;
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
import org.scooterrental.repository.daointerface.RentalPointDao;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.scooterrental.repository.daointerface.TariffDao;
import org.scooterrental.repository.daointerface.TripDao;
import org.scooterrental.repository.daointerface.UserDao;
import org.scooterrental.service.dto.TripCreateDto;
import org.scooterrental.service.dto.TripResponseDto;
import org.scooterrental.service.mapper.TripMapper;
import org.scooterrental.service.serviceinterface.UserService;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @Mock
    private TripDao tripDao;

    @Mock
    private UserDao userDao;

    @Mock
    private ScooterDao scooterDao;

    @Mock
    private TariffDao tariffDao;

    @Mock
    private RentalPointDao rentalPointDao;

    @Mock
    private TripMapper tripMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private TripServiceImpl tripService;

    @Test
    void startTrip_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;
        Long scooterId = 2L;
        Long tariffId = 3L;
        Long tripId = 4L;

        User user = new User();
        user.setUserId(userId);
        user.setBalance(new BigDecimal(500));

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);
        tariff.setPaymentType(PaymentType.HOURLY);

        Trip trip = new Trip();
        trip.setTripId(tripId);
        trip.setScooter(scooter);
        trip.setUser(user);
        trip.setTariff(tariff);

        TripResponseDto expected = new TripResponseDto();
        expected.setTripId(tripId);
        expected.setTripStatus("COMPLETED");
        expected.setUserId(userId);
        expected.setScooterId(scooterId);
        expected.setTariffId(tariffId);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setScooterId(scooterId);
        tripCreateDto.setTariffId(tariffId);
        tripCreateDto.setUserId(userId);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(tariffDao.findTariff(tariffId)).thenReturn(tariff);
        when(tripMapper.toTripDto(any(Trip.class))).thenReturn(expected);
        when(tripDao.isThereActiveTripByUserId(userId)).thenReturn(false);

        TripResponseDto actual = tripService.startTrip(tripCreateDto);

        assertNotNull(actual);
        assertEquals(expected.getTripId(), actual.getTripId());
        assertEquals(expected.getScooterId(), actual.getScooterId());
        assertEquals(expected.getTariffId(), actual.getTariffId());
        assertEquals(expected.getUserId(), actual.getUserId());

        verify(userDao, times(1)).findUserById(userId);
        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tripDao, times(1)).create(any(Trip.class));
        verify(tripMapper, times(1)).toTripDto(any(Trip.class));
    }

    @Test
    void startTrip_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 1L;
        Long scooterId = 2L;
        Long tariffId = 3L;
        Long tripId = 4L;

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setTariffId(tariffId);
        tripCreateDto.setScooterId(scooterId);

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> tripService.startTrip(tripCreateDto));

        verify(userDao, times(1)).findUserById(userId);
        verify(scooterDao, never()).findScooter(scooterId);
        verify(tariffDao, never()).findTariff(tariffId);
        verify(tripDao, never()).create(any(Trip.class));
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void startTrip_ShouldThrowBannedExceptionException_WhenUserBanned() {
        Long userId = 1L;
        Long scooterId = 2L;
        Long tariffId = 3L;
        Long tripId = 4L;

        User user = new User();
        user.setUserId(userId);
        user.setBanReason(BanReason.VANDALISM);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setTariffId(tariffId);
        tripCreateDto.setScooterId(scooterId);

        when(userDao.findUserById(userId)).thenReturn(user);

        assertThrows(UserBannedException.class, () -> tripService.startTrip(tripCreateDto));

        verify(userDao, times(1)).findUserById(userId);
        verify(scooterDao, never()).findScooter(scooterId);
        verify(tariffDao, never()).findTariff(tariffId);
        verify(tripDao, never()).create(any(Trip.class));
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void startTrip_ShouldThrowValueLessZeroException_WhenBalanceLessZero() {
        Long userId = 1L;
        Long scooterId = 2L;
        Long tariffId = 3L;
        Long tripId = 4L;

        User user = new User();
        user.setUserId(userId);
        user.setBalance(new BigDecimal(-500));

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setTariffId(tariffId);
        tripCreateDto.setScooterId(scooterId);

        when(userDao.findUserById(userId)).thenReturn(user);

        assertThrows(ValueLessZeroException.class, () -> tripService.startTrip(tripCreateDto));

        verify(userDao, times(1)).findUserById(userId);
        verify(scooterDao, never()).findScooter(scooterId);
        verify(tariffDao, never()).findTariff(tariffId);
        verify(tripDao, never()).create(any(Trip.class));
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void startTrip_ShouldThrowScooterNotFoundException_WhenScooterNotFound() {
        Long userId = 1L;
        Long scooterId = 2L;
        Long tariffId = 3L;
        Long tripId = 4L;

        User user = new User();
        user.setUserId(userId);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setTariffId(tariffId);
        tripCreateDto.setScooterId(scooterId);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(scooterDao.findScooter(scooterId)).thenReturn(null);

        assertThrows(ScooterNotFoundException.class, () -> tripService.startTrip(tripCreateDto));

        verify(userDao, times(1)).findUserById(userId);
        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(tariffDao, never()).findTariff(tariffId);
        verify(tripDao, never()).create(any(Trip.class));
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void startTrip_ShouldThrowScooterNotAvailableException_WhenScooterNotAvailable() {
        Long userId = 1L;
        Long scooterId = 2L;
        Long tariffId = 3L;
        Long tripId = 4L;

        User user = new User();
        user.setUserId(userId);

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setScooterStatus(ScooterStatus.IN_SERVICE);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setTariffId(tariffId);
        tripCreateDto.setScooterId(scooterId);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);

        assertThrows(ScooterNotAvailableException.class, () -> tripService.startTrip(tripCreateDto));

        verify(userDao, times(1)).findUserById(userId);
        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(tariffDao, never()).findTariff(tariffId);
        verify(tripDao, never()).create(any(Trip.class));
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void startTrip_ShouldThrowLowBatteryLevelException_WhenBatteryLevelLessTen() {
        Long userId = 1L;
        Long scooterId = 2L;
        Long tariffId = 3L;
        Long tripId = 4L;

        User user = new User();
        user.setUserId(userId);

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);
        scooter.setBatteryLevel(7);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setTariffId(tariffId);
        tripCreateDto.setScooterId(scooterId);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);

        assertThrows(LowBatteryLevelException.class, () -> tripService.startTrip(tripCreateDto));

        verify(userDao, times(1)).findUserById(userId);
        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(tariffDao, never()).findTariff(tariffId);
        verify(tripDao, never()).create(any(Trip.class));
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void startTrip_ShouldThrowTariffNotFoundException_WhenTariffNotFound() {
        Long userId = 1L;
        Long scooterId = 2L;
        Long tariffId = 3L;
        Long tripId = 4L;

        User user = new User();
        user.setUserId(userId);

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setTariffId(tariffId);
        tripCreateDto.setScooterId(scooterId);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(tariffDao.findTariff(tariffId)).thenReturn(null);

        assertThrows(TariffNotFoundException.class, () -> tripService.startTrip(tripCreateDto));

        verify(userDao, times(1)).findUserById(userId);
        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tripDao, never()).create(any(Trip.class));
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void startTrip_ShouldThrowUserHasNotActiveSeasonTicketException_WhenTariffIsSeasonTicketAndUserDotHaveIt() {
        Long userId = 1L;
        Long scooterId = 2L;
        Long tariffId = 3L;
        Long tripId = 4L;

        User user = new User();
        user.setUserId(userId);

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setTariffId(tariffId);
        tripCreateDto.setScooterId(scooterId);

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);
        tariff.setPaymentType(PaymentType.SEASON_TICKET);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(tariffDao.findTariff(tariffId)).thenReturn(tariff);

        assertThrows(UserHasNoActiveSeasonTicketException.class, () -> tripService.startTrip(tripCreateDto));

        verify(userDao, times(1)).findUserById(userId);
        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tripDao, never()).create(any(Trip.class));
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void startTrip_ShouldThrowUserAlreadyHasActiveTripException_WhenUserHaveTripAlready() {
        Long userId = 1L;
        Long scooterId = 2L;
        Long tariffId = 3L;
        Long tripId = 4L;

        User user = new User();
        user.setUserId(userId);

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setTariffId(tariffId);
        tripCreateDto.setScooterId(scooterId);

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);
        tariff.setPaymentType(PaymentType.HOURLY);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(tariffDao.findTariff(tariffId)).thenReturn(tariff);
        when(tripDao.isThereActiveTripByUserId(userId)).thenReturn(true);

        assertThrows(UserAlreadyHasActiveTripException.class, () -> tripService.startTrip(tripCreateDto));

        verify(userDao, times(1)).findUserById(userId);
        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tripDao, never()).create(any(Trip.class));
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void finishTrip_ShouldReturnDto_WhenAllCorrect() {
        Long tripId = 1L;
        Long endRentalPointId = 2L;
        Long tariffId = 3L;
        Long userId = 4L;

        User user = new User();
        user.setUserId(userId);

        RentalPoint endRentalPoint = new RentalPoint();
        endRentalPoint.setRentalPointId(endRentalPointId);
        endRentalPoint.setRentalPointType(RentalPointType.BUILDING);

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);
        tariff.setPaymentType(PaymentType.HOURLY);

        Scooter scooter = new Scooter();
        scooter.setScooterStatus(ScooterStatus.IN_RENT);

        Trip trip = new Trip();
        trip.setTripId(tripId);
        trip.setUser(user);
        trip.setStartTime(LocalDateTime.now().minusMinutes(30));
        trip.setTariff(tariff);
        trip.setScooter(scooter);

        TripResponseDto expected = new TripResponseDto();
        expected.setTripId(tripId);
        expected.setUserId(userId);
        expected.setEndPointId(endRentalPointId);

        when(tripDao.findTrip(tripId)).thenReturn(trip);
        when(rentalPointDao.findRentalPointById(endRentalPointId)).thenReturn(endRentalPoint);
        when(tripMapper.toTripDto(any(Trip.class))).thenReturn(expected);

        TripResponseDto actual = tripService.finishTrip(tripId, endRentalPointId, userId);

        assertNotNull(actual);
        assertEquals(expected.getTripId(), actual.getTripId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getEndPointId(), actual.getEndPointId());

        verify(tripDao, times(1)).findTrip(tripId);
        verify(rentalPointDao, times(1)).findRentalPointById(anyLong());
        verify(tripMapper, times(1)).toTripDto(any(Trip.class));
    }

    @Test
    void finishTrip_ShouldThrowTripNotFoundException_WhenTripNotFound() {
        Long tripId = 1L;
        Long endRentalPointId = 2L;
        Long userId = 3L;

        when(tripDao.findTrip(tripId)).thenReturn(null);

        assertThrows(TripNotFoundException.class, () -> tripService.finishTrip(tripId, endRentalPointId, userId));

        verify(tripDao, times(1)).findTrip(tripId);
        verify(rentalPointDao, never()).findRentalPointById(anyLong());
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void finishTrip_ShouldThrowAccessDeniedException_WhenOtherUserTryToFinishTrip() {
        Long tripId = 1L;
        Long endRentalPointId = 2L;
        Long userId1 = 4L;
        Long userId2 = 5L;

        User user1 = new User();
        user1.setUserId(userId1);

        Trip trip = new Trip();
        trip.setTripId(tripId);
        trip.setUser(user1);

        when(tripDao.findTrip(tripId)).thenReturn(trip);

        assertThrows(AccessDeniedException.class, () -> tripService.finishTrip(tripId, endRentalPointId, userId2));

        verify(tripDao, times(1)).findTrip(tripId);
        verify(rentalPointDao, never()).findRentalPointById(anyLong());
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void finishTrip_ShouldThrowTripAlreadyCompletedException_WhenTripNotActive() {
        Long tripId = 1L;
        Long endRentalPointId = 2L;
        Long userId = 4L;

        User user = new User();
        user.setUserId(userId);

        Trip trip = new Trip();
        trip.setTripId(tripId);
        trip.setUser(user);
        trip.setTripStatus(TripStatus.COMPLETED);

        when(tripDao.findTrip(tripId)).thenReturn(trip);

        assertThrows(TripAlreadyCompletedException.class, () -> tripService.finishTrip(tripId, endRentalPointId, userId));

        verify(tripDao, times(1)).findTrip(tripId);
        verify(rentalPointDao, never()).findRentalPointById(anyLong());
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void finishTrip_ShouldThrowRentalPointNotFoundException_WhenEndRentalPointNotFound() {
        Long tripId = 1L;
        Long endRentalPointId = 2L;
        Long userId = 4L;

        User user = new User();
        user.setUserId(userId);

        Trip trip = new Trip();
        trip.setTripId(tripId);
        trip.setUser(user);

        when(tripDao.findTrip(tripId)).thenReturn(trip);
        when(rentalPointDao.findRentalPointById(endRentalPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> tripService.finishTrip(tripId, endRentalPointId, userId));

        verify(tripDao, times(1)).findTrip(tripId);
        verify(rentalPointDao, times(1)).findRentalPointById(anyLong());
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void emergencyFinishTrip_ShouldReturnDto_WhenAllCorrect() {
        Long tripId = 1L;
        Long tariffId = 3L;
        Long userId = 4L;

        User user = new User();
        user.setUserId(userId);

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);
        tariff.setPaymentType(PaymentType.HOURLY);

        Scooter scooter = new Scooter();
        scooter.setScooterStatus(ScooterStatus.IN_RENT);

        Trip trip = new Trip();
        trip.setTripId(tripId);
        trip.setUser(user);
        trip.setStartTime(LocalDateTime.now().minusMinutes(30));
        trip.setTariff(tariff);
        trip.setScooter(scooter);

        TripResponseDto expected = new TripResponseDto();
        expected.setTripId(tripId);
        expected.setUserId(userId);

        when(tripDao.findTrip(tripId)).thenReturn(trip);
        when(tripMapper.toTripDto(any(Trip.class))).thenReturn(expected);

        TripResponseDto actual = tripService.emergencyFinishTrip(tripId);

        assertNotNull(actual);
        assertEquals(expected.getTripId(), actual.getTripId());
        assertEquals(expected.getUserId(), actual.getUserId());

        verify(tripDao, times(1)).findTrip(tripId);
        verify(tripMapper, times(1)).toTripDto(any(Trip.class));
    }

    @Test
    void emergencyFinishTrip_ShouldThrowTripAlreadyCompletedException_WhenTripNotActive() {
        Long tripId = 1L;

        Trip trip = new Trip();
        trip.setTripId(tripId);
        trip.setTripStatus(TripStatus.COMPLETED);

        when(tripDao.findTrip(tripId)).thenReturn(trip);

        assertThrows(TripAlreadyCompletedException.class, () -> tripService.emergencyFinishTrip(tripId));

        verify(tripDao, times(1)).findTrip(tripId);
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void getTrip_ShouldReturnDto_WhenAllCorrect() {
        Long tripId = 1L;

        Trip trip = new Trip();
        trip.setTripId(tripId);

        TripResponseDto expected = new TripResponseDto();
        expected.setTripId(tripId);

        when(tripDao.findTrip(tripId)).thenReturn(trip);
        when(tripMapper.toTripDto(trip)).thenReturn(expected);

        TripResponseDto actual = tripService.getTrip(tripId);

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(tripDao, times(1)).findTrip(tripId);
        verify(tripMapper, times(1)).toTripDto(trip);
    }

    @Test
    void getTrip_ShouldThrowTripNotFoundException_WhenTripNotFound() {
        Long tripId = 1L;

        when(tripDao.findTrip(tripId)).thenReturn(null);

        assertThrows(TripNotFoundException.class, () -> tripService.getTrip(tripId));

        verify(tripDao, times(1)).findTrip(tripId);
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void getAllTrips_ShouldReturnListOfDto_WhenAllCorrect() {
        Long tripId1 = 1L;
        Long tripId2 = 2L;

        Trip trip1 = new Trip();
        trip1.setTripId(tripId1);

        Trip trip2 = new Trip();
        trip2.setTripId(tripId2);

        TripResponseDto tripResponseDto1 = new TripResponseDto();
        tripResponseDto1.setTripId(tripId1);

        TripResponseDto tripResponseDto2 = new TripResponseDto();
        tripResponseDto2.setTripId(tripId2);

        List<TripResponseDto> expected = new ArrayList<>(List.of(tripResponseDto1, tripResponseDto2));

        when(tripDao.findTrips()).thenReturn(new ArrayList<>(List.of(trip1, trip2)));
        when(tripMapper.toTripDto(trip1)).thenReturn(tripResponseDto1);
        when(tripMapper.toTripDto(trip2)).thenReturn(tripResponseDto2);

        List<TripResponseDto> actual = tripService.getAllTrips();

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(tripDao, times(1)).findTrips();
        verify(tripMapper, times(2)).toTripDto(any(Trip.class));
    }

    @Test
    void getUserHistory_ShouldReturnListOfDto_WhenAllCorrect() {
        Long userId = 3L;
        Long tripId1 = 1L;
        Long tripId2 = 2L;

        Trip trip1 = new Trip();
        trip1.setTripId(tripId1);

        Trip trip2 = new Trip();
        trip2.setTripId(tripId2);

        User user = new User();
        user.setUserId(userId);

        TripResponseDto tripResponseDto1 = new TripResponseDto();
        tripResponseDto1.setTripId(tripId1);

        TripResponseDto tripResponseDto2 = new TripResponseDto();
        tripResponseDto2.setTripId(tripId2);

        List<TripResponseDto> expected = new ArrayList<>(List.of(tripResponseDto1, tripResponseDto2));

        when(userDao.findUserById(userId)).thenReturn(user);
        when(tripDao.findTripsByUserId(userId)).thenReturn(new ArrayList<>(List.of(trip1, trip2)));
        when(tripMapper.toTripDto(trip1)).thenReturn(tripResponseDto1);
        when(tripMapper.toTripDto(trip2)).thenReturn(tripResponseDto2);

        List<TripResponseDto> actual = tripService.getUserHistory(userId);

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(userDao, times(1)).findUserById(userId);
        verify(tripDao, times(1)).findTripsByUserId(userId);
        verify(tripMapper, times(2)).toTripDto(any(Trip.class));
    }

    @Test
    void getUserHistory_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 3L;

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> tripService.getUserHistory(userId));

        verify(userDao, times(1)).findUserById(userId);
        verify(tripDao, never()).findTripsByUserId(userId);
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }

    @Test
    void getScooterHistory_ShouldReturnListOfDto_WhenAllCorrect() {
        Long scooterId = 3L;
        Long tripId1 = 1L;
        Long tripId2 = 2L;

        Trip trip1 = new Trip();
        trip1.setTripId(tripId1);

        Trip trip2 = new Trip();
        trip2.setTripId(tripId2);

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);

        TripResponseDto tripResponseDto1 = new TripResponseDto();
        tripResponseDto1.setTripId(tripId1);

        TripResponseDto tripResponseDto2 = new TripResponseDto();
        tripResponseDto2.setTripId(tripId2);

        List<TripResponseDto> expected = new ArrayList<>(List.of(tripResponseDto1, tripResponseDto2));

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(tripDao.findTripsByScooterId(scooterId)).thenReturn(new ArrayList<>(List.of(trip1, trip2)));
        when(tripMapper.toTripDto(trip1)).thenReturn(tripResponseDto1);
        when(tripMapper.toTripDto(trip2)).thenReturn(tripResponseDto2);

        List<TripResponseDto> actual = tripService.getScooterHistory(scooterId);

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(tripDao, times(1)).findTripsByScooterId(scooterId);
        verify(tripMapper, times(2)).toTripDto(any(Trip.class));
    }

    @Test
    void getScooterHistory_ShouldThrowScooterNotFoundException_WhenScooterNotFound() {
        Long scooterId = 3L;

        when(scooterDao.findScooter(scooterId)).thenReturn(null);

        assertThrows(ScooterNotFoundException.class, () -> tripService.getScooterHistory(scooterId));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(tripDao, never()).findTripsByScooterId(scooterId);
        verify(tripMapper, never()).toTripDto(any(Trip.class));
    }
}

