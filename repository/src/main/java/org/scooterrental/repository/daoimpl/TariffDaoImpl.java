package org.scooterrental.repository.daoimpl;

import org.hibernate.SessionFactory;
import org.scooterrental.model.entity.Tariff;
import org.scooterrental.repository.daointerface.TariffDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TariffDaoImpl implements TariffDao {
    private final SessionFactory sessionFactory;

    public TariffDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Tariff tariff) {
        sessionFactory.getCurrentSession().persist(tariff);
    }

    @Override
    public void update(Tariff tariff) {
        sessionFactory.getCurrentSession().merge(tariff);
    }

    @Override
    public Tariff findTariff(Long tariffId) {
        return sessionFactory.getCurrentSession().get(Tariff.class, tariffId);
    }

    @Override
    public List<Tariff> findTariffs() {
        return sessionFactory.getCurrentSession().createQuery("FROM Tariff", Tariff.class).list();
    }
}
