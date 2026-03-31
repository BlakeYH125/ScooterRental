package org.scooterrental.model.entity;

import jakarta.persistence.*;
import org.scooterrental.model.eums.ScooterStatus;

@Entity
@Table(name = "scooters")
public class Scooter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scooter_id")
    private Long scooterId;

    @Column(name = "model")
    private String model;

    @Column(name = "battery_level")
    private int batteryLevel;

    @Column(name = "scooter_status")
    @Enumerated(EnumType.STRING)
    private ScooterStatus scooterStatus;

    @ManyToOne
    @JoinColumn(name = "rental_point_id")
    private RentalPoint rentalPoint;

    public Scooter() {
    }

    public Scooter(String model, int batteryLevel, ScooterStatus scooterStatus, RentalPoint rentalPoint) {
        this.model = model;
        this.batteryLevel = batteryLevel;
        this.scooterStatus = scooterStatus;
        this.rentalPoint = rentalPoint;
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
}
