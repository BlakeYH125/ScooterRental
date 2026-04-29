package org.scooterrental.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TripResponseDto {
    private Long tripId;
    private Long userId;
    private Long scooterId;
    private String tripStatus;
    private Long startPointId;
    private Long endPointId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalCost;
    private double mileage;
    private Long tariffId;
}
