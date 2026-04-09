package org.scooterrental.repository.daointerface;

import org.scooterrental.model.entity.Trip;

import java.util.List;

public interface TripDao {
    void create(Trip trip);
    void update(Trip trip);
    Trip findTrip(Long tripId);
    List<Trip> findTrips();
    boolean isThereActiveTripByUserId(Long userId);
    List<Trip> findTripsByUserId(Long userId);
    List<Trip> findTripsByScooterId(Long scooterId);
}
