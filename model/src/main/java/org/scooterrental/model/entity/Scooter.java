package org.scooterrental.model.entity;

import jakarta.persistence.*;
import org.scooterrental.model.enums.ScooterStatus;

@Entity
@Table(name = "scooters")
public class Scooter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scooter_id")
    private Long scooterId;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "battery_level", nullable = false)
    private int batteryLevel = 100;

    @Column(name = "scooter_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ScooterStatus scooterStatus = ScooterStatus.IN_WAREHOUSE;

    @ManyToOne
    @JoinColumn(name = "rental_point_id")
    private RentalPoint rentalPoint = null;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public Scooter() {
    }

    public Scooter(String model) {
        this.model = model;
    }

    public Long getScooterId() {
        return scooterId;
    }

    public void setScooterId(Long scooterId) {
        this.scooterId = scooterId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public ScooterStatus getScooterStatus() {
        return scooterStatus;
    }

    public void setScooterStatus(ScooterStatus scooterStatus) {
        this.scooterStatus = scooterStatus;
    }

    public RentalPoint getRentalPoint() {
        return rentalPoint;
    }

    public void setRentalPoint(RentalPoint rentalPoint) {
        this.rentalPoint = rentalPoint;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
