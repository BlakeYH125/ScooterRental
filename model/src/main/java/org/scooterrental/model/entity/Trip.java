package org.scooterrental.model.entity;

import jakarta.persistence.*;

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
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "scooter_id")
    private Scooter scooter;

    @ManyToOne
    @JoinColumn(name = "start_point_id")
    private RentalPoint startPoint;

    @ManyToOne
    @JoinColumn(name = "end_point_id")
    private RentalPoint endPoint;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    public Trip() {
    }

    public Trip(User user, Scooter scooter, RentalPoint startPoint, RentalPoint endPoint, LocalDateTime startTime, LocalDateTime endTime, BigDecimal totalCost, Tariff tariff) {
        this.user = user;
        this.scooter = scooter;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalCost = totalCost;
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
