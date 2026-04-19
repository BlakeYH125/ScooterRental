package org.scooterrental.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import org.scooterrental.model.enums.RentalPointType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rental_points")
public class RentalPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_point_id")
    private Long rentalPointId;

    @Column(name = "rental_point_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RentalPointType rentalPointType;

    @Column(name = "location", nullable = false)
    private String location;

    @ManyToOne
    @JoinColumn(name = "parent_point_id")
    private RentalPoint parentPoint;

    @OneToMany(mappedBy = "parentPoint")
    private List<RentalPoint> childPoints = new ArrayList<>();

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public RentalPoint() {
    }

    public RentalPoint(RentalPointType rentalPointType, String location, RentalPoint parentPoint) {
        this.rentalPointType = rentalPointType;
        this.location = location;
        this.parentPoint = parentPoint;
    }

    public Long getRentalPointId() {
        return rentalPointId;
    }

    public void setRentalPointId(Long rentalPointId) {
        this.rentalPointId = rentalPointId;
    }

    public RentalPointType getRentalPointType() {
        return rentalPointType;
    }

    public void setRentalPointType(RentalPointType rentalPointType) {
        this.rentalPointType = rentalPointType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public RentalPoint getParentPoint() {
        return parentPoint;
    }

    public void setParentPoint(RentalPoint parentPoint) {
        this.parentPoint = parentPoint;
    }

    public List<RentalPoint> getChildPoints() {
        return childPoints;
    }

    public void setChildPoints(List<RentalPoint> childPoints) {
        this.childPoints = childPoints;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
