package org.scooterrental.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.scooterrental.model.enums.ScooterStatus;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "mileage", nullable = false)
    private double mileage = 0.0;
}
