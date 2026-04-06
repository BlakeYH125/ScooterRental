package org.scooterrental.repository.daoimpl;

import org.hibernate.SessionFactory;
import org.scooterrental.model.entity.Scooter;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScooterDaoImpl implements ScooterDao {
    private final SessionFactory sessionFactory;

    public ScooterDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Scooter scooter) {
        sessionFactory.getCurrentSession().persist(scooter);
    }

    @Override
    public void update(Scooter scooter) {
        sessionFactory.getCurrentSession().merge(scooter);
    }

    @Override
    public boolean delete(Long scooterId) {
        Scooter scooter = findScooter(scooterId);
        if (scooter == null) {
            return false;
        }
        sessionFactory.getCurrentSession().remove(scooter);
        return true;
    }

    @Override
    public Scooter findScooter(Long scooterId) {
        return sessionFactory.getCurrentSession().get(Scooter.class, scooterId);
    }

    @Override
    public List<Scooter> findScooters() {
        return sessionFactory.getCurrentSession().createQuery("FROM Scooter", Scooter.class).list();
    }

    @Override
    public Long countScootersAtRentalPoint(Long rentalPointId) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(s) FROM Scooter s WHERE s.rentalPoint.rentalPointId = :rentalPointId", Long.class)
                .setParameter("rentalPointId", rentalPointId)
                .getSingleResult();
    }
}
