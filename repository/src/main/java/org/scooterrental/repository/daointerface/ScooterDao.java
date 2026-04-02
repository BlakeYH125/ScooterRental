package org.scooterrental.repository.daointerface;

import org.scooterrental.model.entity.Scooter;

import java.util.List;

public interface ScooterDao {
    void create(Scooter scooter);
    void update(Scooter scooter);
    boolean delete(Long scooterId);
    Scooter findScooter(Long scooterId);
    List<Scooter> findScooters();
}
