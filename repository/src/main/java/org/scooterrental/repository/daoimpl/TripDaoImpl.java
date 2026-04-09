package org.scooterrental.repository.daoimpl;

import org.hibernate.SessionFactory;
import org.scooterrental.model.entity.Trip;
import org.scooterrental.model.enums.TripStatus;
import org.scooterrental.repository.daointerface.TripDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TripDaoImpl implements TripDao {
    private final SessionFactory sessionFactory;

    public TripDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Trip trip) {
        sessionFactory.getCurrentSession().persist(trip);
    }

    @Override
    public void update(Trip trip) {
        sessionFactory.getCurrentSession().merge(trip);
    }

    @Override
    public Trip findTrip(Long tripId) {
        return sessionFactory.getCurrentSession().get(Trip.class, tripId);
    }

    @Override
    public List<Trip> findTrips() {
        return sessionFactory.getCurrentSession().createQuery("FROM Trip", Trip.class).list();
    }

    @Override
    public boolean isThereActiveTripByUserId(Long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(t) FROM Trip t WHERE user.userId = :userId AND t.tripStatus = :tripStatus", Long.class)
                .setParameter("userId", userId)
                .setParameter("tripStatus", TripStatus.ACTIVE)
                .getSingleResult() > 0;
    }

    @Override
    public List<Trip> findTripsByUserId(Long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Trip WHERE user.userId = :userId", Trip.class)
                .setParameter("userId", userId)
                .list();
    }

    @Override
    public List<Trip> findTripsByScooterId(Long scooterId) {
        return sessionFactory.getCurrentSession()
                .createQuery("From Trip Where scooter.scooterId = :scooterId", Trip.class)
                .setParameter("scooterId", scooterId)
                .list();
    }
}
