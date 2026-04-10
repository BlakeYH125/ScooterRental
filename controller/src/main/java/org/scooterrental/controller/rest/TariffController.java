package org.scooterrental.controller.rest;

import jakarta.validation.Valid;
import org.scooterrental.model.enums.PaymentType;
import org.scooterrental.service.dto.TariffCreateDto;
import org.scooterrental.service.dto.TariffResponseDto;
import org.scooterrental.service.serviceinterface.TariffService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/scooter-rental/tariffs")
public class TariffController {
    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TariffResponseDto> addNewTariff(@Valid @RequestBody TariffCreateDto tariffCreateDto) {
        return ResponseEntity.ok().body(tariffService.addNewTariff(tariffCreateDto));
    }

    @PatchMapping("/{tariffId}/set-new-payment-type")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TariffResponseDto> setNewPaymentType(@PathVariable("tariffId") Long tariffId, @RequestParam("newPaymentType") PaymentType newPaymentType) {
        return ResponseEntity.ok().body(tariffService.setNewPaymentType(tariffId, newPaymentType));
    }

    @PatchMapping("/{tariffId}/set-new-price")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TariffResponseDto> setNewPrice(@PathVariable("tariffId") Long tariffId, @RequestParam("newPrice") BigDecimal newPrice) {
        return ResponseEntity.ok().body(tariffService.setNewPrice(tariffId, newPrice));
    }

    @PatchMapping("/{tariffId}/set-new-discount")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TariffResponseDto> setNewDiscount(@PathVariable("tariffId") Long tariffId, @RequestParam("newDiscount") int newDiscount) {
        return ResponseEntity.ok().body(tariffService.setNewDiscount(tariffId, newDiscount));
    }

    @GetMapping("/{tariffId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<TariffResponseDto> getTariff(@PathVariable("tariffId") Long tariffId) {
        return ResponseEntity.ok().body(tariffService.getTariff(tariffId));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<TariffResponseDto>> getAllTariffs() {
        return ResponseEntity.ok().body(tariffService.getAllTariffs());
    }
}
