package org.scooterrental.service.serviceimpl.simulation;

import org.scooterrental.model.entity.Scooter;
import org.scooterrental.model.entity.Trip;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.scooterrental.repository.daointerface.TripDao;
import org.scooterrental.service.serviceinterface.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BatterySimulationService {
    private final TripDao tripDao;
    private final ScooterDao scooterDao;
    private final TripService tripService;
    private static final Logger logger = LoggerFactory.getLogger(BatterySimulationService.class);

    public BatterySimulationService(TripDao tripDao, ScooterDao scooterDao, TripService tripService) {
        this.tripDao = tripDao;
        this.scooterDao = scooterDao;
        this.tripService = tripService;
    }

    @Scheduled(fixedRate = 15000)
    @Transactional
    public void simulateBatteryDrain() {
        List<Trip> activeTrips = tripDao.findActiveTrips();
        for (Trip trip : activeTrips) {
            Scooter scooter = trip.getScooter();
            scooter.setBatteryLevel(scooter.getBatteryLevel() - 1);
            if (scooter.getBatteryLevel() <= 5) {
                tripService.emergencyFinishTrip(trip.getTripId());
            } else {
                scooterDao.update(scooter);
            }
        }
        if (!activeTrips.isEmpty()) {
            logger.info("Обновлен процент заряда у {} самокатов", activeTrips.size());
        }
    }
}
