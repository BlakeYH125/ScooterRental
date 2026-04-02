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
        RentalPoint rentalPoint = findRentalPoint(rentalPointId);
        if (rentalPoint == null) {
            return false;
        }
        sessionFactory.getCurrentSession().remove(rentalPoint);
        return true;
    }

    @Override
    public RentalPoint findRentalPoint(Long rentalPointId) {
        return sessionFactory.getCurrentSession().get(RentalPoint.class, rentalPointId);
    }

    @Override
    public List<RentalPoint> findRentalPoints() {
        return sessionFactory.getCurrentSession().createQuery("FROM RentalPoint", RentalPoint.class).list();
    }
}
