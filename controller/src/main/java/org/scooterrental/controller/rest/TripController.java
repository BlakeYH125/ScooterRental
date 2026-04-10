package org.scooterrental.controller.rest;

import jakarta.validation.Valid;
import org.scooterrental.model.entity.User;
import org.scooterrental.service.dto.TripCreateDto;
import org.scooterrental.service.dto.TripResponseDto;
import org.scooterrental.service.serviceinterface.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scooter-rental/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER' and authentication.principal.userId == #tripCreateDto.userId)")
    @PostMapping("/start")
    public ResponseEntity<TripResponseDto> startTrip(@Valid @RequestBody TripCreateDto tripCreateDto) {
        return ResponseEntity.ok().body(tripService.startTrip(tripCreateDto));
    }

    @PatchMapping("/{tripId}/finish")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TripResponseDto> finishTrip(@PathVariable("tripId") Long tripId,
                                                      @RequestParam("endRentalPointId") Long endRentalPointId,
                                                      @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(tripService.finishTrip(tripId, endRentalPointId, user.getUserId()));
    }

    @PatchMapping("/{tripId}/emergency-finish")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TripResponseDto> emergencyFinishTrip(@PathVariable("tripId") Long tripId) {
        return ResponseEntity.ok().body(tripService.emergencyFinishTrip(tripId));
    }

    @GetMapping("/{tripId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TripResponseDto> getTrip(@PathVariable("tripId") Long tripId) {
        return ResponseEntity.ok().body(tripService.getTrip(tripId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<TripResponseDto>> getAllTrips() {
        return ResponseEntity.ok().body(tripService.getAllTrips());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or authentication.principal.userId == #userId")
    @GetMapping("/user-history/{userId}")
    public ResponseEntity<List<TripResponseDto>> getUserHistory(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(tripService.getUserHistory(userId));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/scooter-history/{scooterId}")
    public ResponseEntity<List<TripResponseDto>> getScooterHistory(@PathVariable("scooterId") Long scooterId) {
        return ResponseEntity.ok().body(tripService.getScooterHistory(scooterId));
    }
}
