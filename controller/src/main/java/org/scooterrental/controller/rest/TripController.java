package org.scooterrental.controller.rest;

import jakarta.validation.Valid;
import org.scooterrental.model.entity.User;
import org.scooterrental.service.dto.TripCreateDto;
import org.scooterrental.service.dto.TripResponseDto;
import org.scooterrental.service.serviceinterface.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scooter-rental/trips")
public class TripController {
    private final TripService tripService;
    private static final Logger logger = LoggerFactory.getLogger(TripController.class);

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER') and authentication.principal.userId == #tripCreateDto.userId")
    @PostMapping("/start")
    public ResponseEntity<TripResponseDto> startTrip(@Valid @RequestBody TripCreateDto tripCreateDto) {
        logger.info("Получен запрос на старт поездки от пользователя {} на самокате {}", tripCreateDto.getUserId(), tripCreateDto.getScooterId());
        return ResponseEntity.ok().body(tripService.startTrip(tripCreateDto));
    }

    @PatchMapping("/{tripId}/finish")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TripResponseDto> finishTrip(@PathVariable("tripId") Long tripId,
                                                      @RequestParam("endRentalPointId") Long endRentalPointId,
                                                      @AuthenticationPrincipal User user) {
        logger.info("Получен запрос на завершение поездки {}", tripId);
        return ResponseEntity.ok().body(tripService.finishTrip(tripId, endRentalPointId, user.getUserId()));
    }

    @PatchMapping("/{tripId}/emergency-finish")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TripResponseDto> emergencyFinishTrip(@PathVariable("tripId") Long tripId) {
        logger.info("Получен запрос на экстренное завершение поездки {}", tripId);
        return ResponseEntity.ok().body(tripService.emergencyFinishTrip(tripId));
    }

    @GetMapping("/{tripId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TripResponseDto> getTrip(@PathVariable("tripId") Long tripId) {
        logger.info("Получен запрос на информацию о поездке {}", tripId);
        return ResponseEntity.ok().body(tripService.getTrip(tripId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<TripResponseDto>> getAllTrips() {
        logger.info("Получен запрос на информацию о всех поездках");
        return ResponseEntity.ok().body(tripService.getAllTrips());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or authentication.principal.userId == #userId")
    @GetMapping("/user-history/{userId}")
    public ResponseEntity<List<TripResponseDto>> getUserHistory(@PathVariable("userId") Long userId) {
        logger.info("Получен запрос на историю поездок пользователя {}", userId);
        return ResponseEntity.ok().body(tripService.getUserHistory(userId));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/scooter-history/{scooterId}")
    public ResponseEntity<List<TripResponseDto>> getScooterHistory(@PathVariable("scooterId") Long scooterId) {
        logger.info("Получен запрос на историю поездок на самокате {}", scooterId);
        return ResponseEntity.ok().body(tripService.getScooterHistory(scooterId));
    }
}
