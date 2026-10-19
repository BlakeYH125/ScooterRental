package org.scooterrental.repository.daoimpl;

import org.hibernate.SessionFactory;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.enums.RentalPointType;
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

    @Override
    public List<RentalPoint> findBuildingsPage(Long pointId, RentalPointType type, int offset, int limit) {
        String condition = switch (type) {
            case CITY -> "city.rentalPointId = :pointId AND city.deleted = false AND district.deleted = false AND street.deleted = false";
            case DISTRICT -> "district.rentalPointId = :pointId AND district.deleted = false AND street.deleted = false";
            case STREET -> "street.rentalPointId = :pointId AND street.deleted = false";
            case BUILDING -> "building.rentalPointId = :pointId";
        };

        String hql = """
            SELECT building
            FROM RentalPoint building
            LEFT JOIN FETCH building.parentPoint street
            LEFT JOIN street.parentPoint district
            LEFT JOIN district.parentPoint city
            WHERE building.rentalPointType = :buildingType AND building.deleted = false
              AND %s
            ORDER BY building.rentalPointId
            """.formatted(condition);

        return sessionFactory.getCurrentSession().createQuery(hql, RentalPoint.class)
                .setParameter("pointId", pointId)
                .setParameter("buildingType", RentalPointType.BUILDING)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .list();
    }
}
