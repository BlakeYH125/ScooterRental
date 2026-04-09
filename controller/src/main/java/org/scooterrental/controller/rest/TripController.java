package org.scooterrental.controller.rest;

import jakarta.validation.Valid;
import org.scooterrental.service.dto.TripCreateDto;
import org.scooterrental.service.dto.TripResponseDto;
import org.scooterrental.service.serviceinterface.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scooter-rental/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("/start")
    public ResponseEntity<TripResponseDto> startTrip(@Valid @RequestBody TripCreateDto tripCreateDto) {
        return ResponseEntity.ok().body(tripService.startTrip(tripCreateDto));
    }

    @PatchMapping("/{tripId}/finish")
    public ResponseEntity<TripResponseDto> finishTrip(@PathVariable("tripId") Long tripId,
                                                      @RequestParam("endRentalPointId") Long endRentalPointId) {
        return ResponseEntity.ok().body(tripService.finishTrip(tripId, endRentalPointId));
    }

    @PatchMapping("/{tripId}/emergency-finish")
    public ResponseEntity<TripResponseDto> emergencyFinishTrip(@PathVariable("tripId") Long tripId) {
        return ResponseEntity.ok().body(tripService.emergencyFinishTrip(tripId));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponseDto> getTrip(@PathVariable("tripId") Long tripId) {
        return ResponseEntity.ok().body(tripService.getTrip(tripId));
    }

    @GetMapping
    public ResponseEntity<List<TripResponseDto>> getAllTrips() {
        return ResponseEntity.ok().body(tripService.getAllTrips());
    }

    @GetMapping("/user-history/{userId}")
    public ResponseEntity<List<TripResponseDto>> getUserHistory(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(tripService.getUserHistory(userId));
    }

    @GetMapping("/scooter-history/{scooterId}")
    public ResponseEntity<List<TripResponseDto>> getScooterHistory(@PathVariable("scooterId") Long scooterId) {
        return ResponseEntity.ok().body(tripService.getScooterHistory(scooterId));
    }
}
