package org.scooterrental.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rental_points")
public class RentalPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_point_id")
    private Long rentalPointId;

    @Column(name = "location")
    private String location;

    @ManyToOne
    @JoinColumn(name = "parent_point_id")
    private RentalPoint parentPoint;

    public RentalPoint() {
    }

    public RentalPoint(String location, RentalPoint parentPoint) {
        this.location = location;
        this.parentPoint = parentPoint;
    }

    public Long getRentalPoint() {
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
}
