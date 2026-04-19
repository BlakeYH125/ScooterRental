package org.scooterrental.service.serviceimpl.simulation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scooterrental.model.entity.Scooter;
import org.scooterrental.model.entity.Trip;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.scooterrental.repository.daointerface.TripDao;
import org.scooterrental.service.serviceinterface.TripService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class BatterySimulationServiceTest {

    @Mock
    private TripDao tripDao;

    @Mock
    private ScooterDao scooterDao;

    @Mock
    private TripService tripService;

    @InjectMocks
    private BatterySimulationService batterySimulationService;

    @Test
    void simulateBatteryDrain_ShouldDecreaseBatteryAndUpdate_WhenBatteryIsAbove5() {
        Trip trip = new Trip();
        trip.setTripId(1L);
        Scooter scooter = new Scooter();
        scooter.setBatteryLevel(20);
        trip.setScooter(scooter);

        when(tripDao.findActiveTrips()).thenReturn(List.of(trip));

        batterySimulationService.simulateBatteryDrain();

        assertEquals(19, scooter.getBatteryLevel());
        verify(scooterDao, times(1)).update(scooter);
        verify(tripService, never()).emergencyFinishTrip(anyLong());
    }

    @Test
    void simulateBatteryDrain_ShouldEmergencyFinishTrip_WhenBatteryDropsTo5OrBelow() {
        Trip trip = new Trip();
        trip.setTripId(1L);
        Scooter scooter = new Scooter();
        scooter.setBatteryLevel(6);
        trip.setScooter(scooter);

        when(tripDao.findActiveTrips()).thenReturn(List.of(trip));

        batterySimulationService.simulateBatteryDrain();

        assertEquals(5, scooter.getBatteryLevel());
        verify(tripService, times(1)).emergencyFinishTrip(1L);
        verify(scooterDao, never()).update(any(Scooter.class));
    }

    @Test
    void simulateBatteryDrain_ShouldDoNothing_WhenNoActiveTrips() {
        when(tripDao.findActiveTrips()).thenReturn(Collections.emptyList());

        batterySimulationService.simulateBatteryDrain();

        verify(scooterDao, never()).update(any(Scooter.class));
        verify(tripService, never()).emergencyFinishTrip(anyLong());
    }
}