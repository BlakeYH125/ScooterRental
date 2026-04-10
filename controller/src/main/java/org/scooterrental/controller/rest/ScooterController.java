package org.scooterrental.controller.rest;

import jakarta.validation.Valid;
import org.scooterrental.model.enums.ScooterStatus;
import org.scooterrental.service.dto.ScooterCreateDto;
import org.scooterrental.service.dto.ScooterResponseDto;
import org.scooterrental.service.serviceinterface.ScooterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scooter-rental/scooters")
public class ScooterController {

    private final ScooterService scooterService;

    public ScooterController(ScooterService scooterService) {
        this.scooterService = scooterService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ScooterResponseDto> addNewScooter(@Valid @RequestBody ScooterCreateDto scooterCreateDto) {
        return ResponseEntity.ok().body(scooterService.addNewScooter(scooterCreateDto));
    }

    @PatchMapping("/{scooterId}/set-new-model")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ScooterResponseDto> setNewScooterModel(
            @PathVariable("scooterId") Long scooterId,
            @RequestParam("newScooterModel") String newScooterModel) {
        return ResponseEntity.ok().body(scooterService.setNewScooterModel(scooterId, newScooterModel));
    }

    @PatchMapping("/{scooterId}/set-new-battery-level")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ScooterResponseDto> setNewBatteryLevel(
            @PathVariable("scooterId") Long scooterId,
            @RequestParam("newBatteryLevel") int newBatteryLevel) {
        return ResponseEntity.ok().body(scooterService.setNewBatteryLevel(scooterId, newBatteryLevel));
    }

    @PatchMapping("/{scooterId}/recharge-battery")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ScooterResponseDto> rechargeBattery(@PathVariable("scooterId") Long scooterId) {
        return ResponseEntity.ok().body(scooterService.rechargeBattery(scooterId));
    }

    @PatchMapping("/{scooterId}/set-new-scooter-status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ScooterResponseDto> setNewScooterStatus(
            @PathVariable("scooterId") Long scooterId,
            @RequestParam("newScooterStatus")ScooterStatus newScooterStatus) {
        return ResponseEntity.ok().body(scooterService.setNewScooterStatus(scooterId, newScooterStatus));
    }

    @DeleteMapping("/{scooterId}/delete")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteScooter(@PathVariable("scooterId") Long scooterId) {
        scooterService.deleteScooter(scooterId);
        return ResponseEntity.ok().body("Удаление самоката успешно");
    }

    @GetMapping("/{scooterId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ScooterResponseDto> getScooter(@PathVariable("scooterId") Long scooterId) {
        return ResponseEntity.ok().body(scooterService.getScooter(scooterId));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<ScooterResponseDto>> getAllScooters() {
        return ResponseEntity.ok().body(scooterService.getAllScooters());
    }
}
