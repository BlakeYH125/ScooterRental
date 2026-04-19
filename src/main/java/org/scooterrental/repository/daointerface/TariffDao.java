package org.scooterrental.repository.daointerface;

import org.scooterrental.model.entity.Tariff;

import java.util.List;

public interface TariffDao {
    void create(Tariff tariff);
    void update(Tariff tariff);
    Tariff findTariff(Long tariffId);
    List<Tariff> findTariffs();
}
