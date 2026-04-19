package org.scooterrental.service.dto;

public class ScooterResponseDto {
    private Long scooterId;

    private String model;

    private int batteryLevel;

    private String scooterStatus;

    private Long rentalPointId;

    private double mileage;

    public ScooterResponseDto() {
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

    public String getScooterStatus() {
        return scooterStatus;
    }

    public void setScooterStatus(String scooterStatus) {
        this.scooterStatus = scooterStatus;
    }

    public Long getRentalPointId() {
        return rentalPointId;
    }

    public void setRentalPointId(Long rentalPointId) {
        this.rentalPointId = rentalPointId;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }
}
