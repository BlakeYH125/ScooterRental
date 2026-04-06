package org.scooterrental.repository.daoimpl;

import org.hibernate.SessionFactory;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.repository.daointerface.RentalPointDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RentalPointDaoImpl implements RentalPointDao {
    private final SessionFactory sessionFactory;

    public RentalPointDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(RentalPoint rentalPoint) {
        sessionFactory.getCurrentSession().persist(rentalPoint);
    }

    @Override
    public void update(RentalPoint rentalPoint) {
        sessionFactory.getCurrentSession().merge(rentalPoint);
    }

    @Override
    public boolean delete(Long rentalPointId) {
        RentalPoint rentalPoint = findRentalPointById(rentalPointId);
        if (rentalPoint == null) {
            return false;
        }
        rentalPoint.setDeleted(true);
        update(rentalPoint);
        return true;
    }

    @Override
    public RentalPoint findRentalPointById(Long rentalPointId) {
        return sessionFactory.getCurrentSession().get(RentalPoint.class, rentalPointId);
    }

    @Override
    public RentalPoint findRentalPointByLocation(String location) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM RentalPoint WHERE location = :location AND deleted = false", RentalPoint.class)
                .setParameter("location", location).getSingleResultOrNull();
    }

    @Override
    public List<RentalPoint> findRentalPoints() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM RentalPoint WHERE deleted = false", RentalPoint.class)
                .list();
    }
}
