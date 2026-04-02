package org.scooterrental.repository.daointerface;

import org.scooterrental.model.entity.RentalPoint;

import java.util.List;

public interface RentalPointDao {
    void create(RentalPoint rentalPoint);
    void update(RentalPoint rentalPoint);
    boolean delete(Long rentalPointId);
    RentalPoint findRentalPoint(Long rentalPointId);
    List<RentalPoint> findRentalPoints();
}
