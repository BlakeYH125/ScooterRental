package org.scooterrental.repository.daointerface;

import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.enums.RentalPointType;

import java.util.List;

public interface RentalPointDao {
    void create(RentalPoint rentalPoint);
    void update(RentalPoint rentalPoint);
    boolean delete(Long rentalPointId);
    RentalPoint findRentalPointById(Long rentalPointId);
    RentalPoint findRentalPointByLocation(String location);
    List<RentalPoint> findRentalPoints();
    List<RentalPoint> findBuildingsPage(Long pointId, RentalPointType type, int offset, int limit);
}
