package org.scooterrental.controller.rest;

import jakarta.validation.Valid;
import org.scooterrental.service.dto.RentalPointCreateDto;
import org.scooterrental.service.dto.RentalPointDetailsDto;
import org.scooterrental.service.dto.RentalPointResponseDto;
import org.scooterrental.service.serviceinterface.RentalPointService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scooter-rental/rental-points")
public class RentalPointController {
    private final RentalPointService rentalPointService;

    public RentalPointController(RentalPointService rentalPointService) {
        this.rentalPointService = rentalPointService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RentalPointResponseDto> addNewRentalPoint(@Valid @RequestBody RentalPointCreateDto rentalPointCreateDto) {
        return ResponseEntity.ok().body(rentalPointService.addNewRentalPoint(rentalPointCreateDto));
    }

    @PatchMapping("/{rentalPointId}/set-new-location")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RentalPointResponseDto> setNewRentalPointLocation(@PathVariable("rentalPointId") Long rentalPointId,
                                                                            @RequestParam("newLocation") String newLocation) {
        return ResponseEntity.ok().body(rentalPointService.setNewRentalPointLocation(rentalPointId, newLocation));
    }

    @PatchMapping("/{rentalPointId}/set-new-parent-point-id")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RentalPointResponseDto> setNewParentPointId(@PathVariable("rentalPointId") Long rentalPointId,
                                                                            @RequestParam("newParentPointId") Long newParentPointId) {
        return ResponseEntity.ok().body(rentalPointService.setNewParentPointId(rentalPointId, newParentPointId));
    }

    @DeleteMapping("/{rentalPointId}/delete")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteRentalPoint(@PathVariable("rentalPointId") Long rentalPointId) {
        rentalPointService.deleteRentalPoint(rentalPointId);
        return ResponseEntity.ok().body("Удаление точки аренды успешно");
    }

    @GetMapping("/{rentalPointId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<RentalPointResponseDto> getRentalPoint(@PathVariable("rentalPointId") Long rentalPointId) {
        return ResponseEntity.ok().body(rentalPointService.getRentalPoint(rentalPointId));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<RentalPointResponseDto>> getAllRentalPoints() {
        return ResponseEntity.ok().body(rentalPointService.getAllRentalPoints());
    }

    @GetMapping("/{rentalPointId}/details")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<RentalPointDetailsDto> getRentalPointDetails(@PathVariable("rentalPointId") Long rentalPointId) {
        return ResponseEntity.ok().body(rentalPointService.getRentalPointDetails(rentalPointId));
    }
}
