package org.scooterrental.model.entity;

import jakarta.persistence.*;
import org.scooterrental.model.enums.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
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

    @ManyToOne
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    public Trip() {
    }

    public Trip(User user, Scooter scooter, RentalPoint startPoint, LocalDateTime startTime, Tariff tariff) {
        this.user = user;
        this.scooter = scooter;
        this.startPoint = startPoint;
        this.startTime = startTime;
        this.tariff = tariff;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Scooter getScooter() {
        return scooter;
    }

    public void setScooter(Scooter scooter) {
        this.scooter = scooter;
    }

    public TripStatus getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(TripStatus tripStatus) {
        this.tripStatus = tripStatus;
    }

    public RentalPoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(RentalPoint startPoint) {
        this.startPoint = startPoint;
    }

    public RentalPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(RentalPoint endPoint) {
        this.endPoint = endPoint;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }
}
