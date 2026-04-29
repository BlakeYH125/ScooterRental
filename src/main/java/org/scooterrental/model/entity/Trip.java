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
import org.scooterrental.model.enums.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long tripId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "scooter_id", nullable = false)
    private Scooter scooter;

    @Column(name = "trip_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TripStatus tripStatus = TripStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "start_point_id", nullable = false)
    private RentalPoint startPoint;

    @ManyToOne
    @JoinColumn(name = "end_point_id")
    private RentalPoint endPoint;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(name = "mileage", nullable = false)
    private double mileage = 0.0;

    @ManyToOne
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;
}
