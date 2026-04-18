package org.scooterrental.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "rental_points")
public class RentalPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_point_id")
    private Long rentalPointId;

    @Column(name = "location", nullable = false)
    private String location;

    @ManyToOne
    @JoinColumn(name = "parent_point_id")
    private RentalPoint parentPoint;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public RentalPoint() {
    }

    public RentalPoint(String location, RentalPoint parentPoint) {
        this.location = location;
        this.parentPoint = parentPoint;
    }

    public Long getRentalPointId() {
        return rentalPointId;
    }

    public void setRentalPointId(Long rentalPointId) {
        this.rentalPointId = rentalPointId;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
