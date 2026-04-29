package org.scooterrental.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.scooterrental.model.enums.ScooterStatus;
import org.scooterrental.service.dto.ScooterCreateDto;
import org.scooterrental.service.dto.ScooterResponseDto;
import org.scooterrental.service.serviceinterface.ScooterService;
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
@RequiredArgsConstructor
@RequestMapping("/scooter-rental/scooters")
public class ScooterController {
    private final ScooterService scooterService;
    private static final Logger logger = LoggerFactory.getLogger(ScooterController.class);

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ScooterResponseDto> addNewScooter(@Valid @RequestBody ScooterCreateDto scooterCreateDto) {
        logger.info("Получен запрос на добавление нового самоката");
        return ResponseEntity.ok().body(scooterService.addNewScooter(scooterCreateDto));
    }

    @PatchMapping("/{scooterId}/set-new-model")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ScooterResponseDto> setNewScooterModel(
            @PathVariable("scooterId") Long scooterId,
            @RequestParam("newScooterModel") String newScooterModel) {
        logger.info("Получен запрос на установку новой модели у самоката {}", scooterId);
        return ResponseEntity.ok().body(scooterService.setNewScooterModel(scooterId, newScooterModel));
    }

    @PatchMapping("/{scooterId}/put-in-use")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ScooterResponseDto> putScooterInUse(@PathVariable("scooterId") Long scooterId,
                                                              @RequestParam("rentalPointId") Long rentalPointId) {
        logger.info("Получен запрос на вывод самоката {} на использование", scooterId);
        return ResponseEntity.ok().body(scooterService.putScooterInUse(scooterId, rentalPointId));
    }

    @PatchMapping("/{scooterId}/put-in-warehouse")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ScooterResponseDto> putScooterInWarehouse(@PathVariable("scooterId") Long scooterId) {
        logger.info("Получен запрос на вывод самоката {} на склад", scooterId);
        return ResponseEntity.ok().body(scooterService.putScooterInWarehouse(scooterId));
    }

    @PatchMapping("/{scooterId}/set-new-battery-level")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ScooterResponseDto> setNewBatteryLevel(
            @PathVariable("scooterId") Long scooterId,
            @RequestParam("newBatteryLevel") int newBatteryLevel) {
        logger.info("Получен запрос на установку нового процента заряда батареи у самоката {}", scooterId);
        return ResponseEntity.ok().body(scooterService.setNewBatteryLevel(scooterId, newBatteryLevel));
    }

    @PatchMapping("/{scooterId}/recharge-battery")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ScooterResponseDto> rechargeBattery(@PathVariable("scooterId") Long scooterId) {
        logger.info("Получен запрос на перезарядку батареи самоката {}", scooterId);
        return ResponseEntity.ok().body(scooterService.rechargeBattery(scooterId));
    }

    @PatchMapping("/{scooterId}/set-new-scooter-status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ScooterResponseDto> setNewScooterStatus(
            @PathVariable("scooterId") Long scooterId,
            @RequestParam("newScooterStatus") ScooterStatus newScooterStatus) {
        logger.info("Получен запрос на установку нового статуса у самоката {}", scooterId);
        return ResponseEntity.ok().body(scooterService.setNewScooterStatus(scooterId, newScooterStatus));
    }

    @DeleteMapping("/{scooterId}/delete")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteScooter(@PathVariable("scooterId") Long scooterId) {
        logger.info("Получен запрос на удаление самоката {}", scooterId);
        scooterService.deleteScooter(scooterId);
        return ResponseEntity.ok().body("Удаление самоката успешно");
    }

    @GetMapping("/{scooterId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ScooterResponseDto> getScooter(@PathVariable("scooterId") Long scooterId) {
        logger.info("Получен запрос на информацию о самокате {}", scooterId);
        return ResponseEntity.ok().body(scooterService.getScooter(scooterId));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<ScooterResponseDto>> getAllScooters() {
        logger.info("Получен запрос на информацию о всех самокатах");
        return ResponseEntity.ok().body(scooterService.getAllScooters());
    }
}
