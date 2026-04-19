package org.scooterrental.controller.rest;

import jakarta.validation.Valid;
import org.scooterrental.service.dto.RentalPointCreateDto;
import org.scooterrental.service.dto.RentalPointDetailsDto;
import org.scooterrental.service.dto.RentalPointResponseDto;
import org.scooterrental.service.serviceinterface.RentalPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/scooter-rental/rental-points")
public class RentalPointController {
    private final RentalPointService rentalPointService;
    private static final Logger logger = LoggerFactory.getLogger(RentalPointController.class);

    public RentalPointController(RentalPointService rentalPointService) {
        this.rentalPointService = rentalPointService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RentalPointResponseDto> addNewRentalPoint(@Valid @RequestBody RentalPointCreateDto rentalPointCreateDto) {
        logger.info("Получен запрос на добавление новой точки аренды");
        return ResponseEntity.ok().body(rentalPointService.addNewRentalPoint(rentalPointCreateDto));
    }

    @PatchMapping("/{rentalPointId}/set-new-location")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RentalPointResponseDto> setNewRentalPointLocation(@PathVariable("rentalPointId") Long rentalPointId,
                                                                            @RequestParam("newLocation") String newLocation) {
        logger.info("Получен запрос на установку новой локации у точки аренды {}", rentalPointId);
        return ResponseEntity.ok().body(rentalPointService.setNewRentalPointLocation(rentalPointId, newLocation));
    }

    @PatchMapping("/{rentalPointId}/set-new-parent-point-id")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RentalPointResponseDto> setNewParentPointId(@PathVariable("rentalPointId") Long rentalPointId,
                                                                            @RequestParam("newParentPointId") Long newParentPointId) {
        logger.info("Получен запрос на установку новой родительский точки у точки аренды {}", rentalPointId);
        return ResponseEntity.ok().body(rentalPointService.setNewParentPointId(rentalPointId, newParentPointId));
    }

    @DeleteMapping("/{rentalPointId}/delete")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteRentalPoint(@PathVariable("rentalPointId") Long rentalPointId) {
        logger.info("Получен запрос на удаление точки аренды {}", rentalPointId);
        rentalPointService.deleteRentalPoint(rentalPointId);
        return ResponseEntity.ok().body("Удаление точки аренды успешно");
    }

    @GetMapping("/{rentalPointId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<RentalPointResponseDto> getRentalPoint(@PathVariable("rentalPointId") Long rentalPointId) {
        logger.info("Получен запрос на информацию о точке аренды {}", rentalPointId);
        return ResponseEntity.ok().body(rentalPointService.getRentalPoint(rentalPointId));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<RentalPointResponseDto>> getAllRentalPoints() {
        logger.info("Получен запрос на информацию о всех точках аренды");
        return ResponseEntity.ok().body(rentalPointService.getAllRentalPoints());
    }

    @GetMapping("/{rentalPointId}/details")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<RentalPointDetailsDto> getRentalPointDetails(@PathVariable("rentalPointId") Long rentalPointId) {
        logger.info("Получен запрос на получение детальной информации о точке аренды {}", rentalPointId);
        return ResponseEntity.ok().body(rentalPointService.getRentalPointDetails(rentalPointId));
    }

    @GetMapping("/{rentalPointId}/rental-stations")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<RentalPointResponseDto>> getRentalStationsByParentId(@PathVariable("rentalPointId") Long rentalPointId) {
        logger.info("Получен запрос на поиск всех точек около зданий внутри области точки проката {}", rentalPointId);
        return ResponseEntity.ok().body(rentalPointService.getRentalStationsByParentId(rentalPointId));
    }
}
