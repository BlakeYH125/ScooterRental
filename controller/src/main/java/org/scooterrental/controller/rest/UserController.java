package org.scooterrental.controller.rest;

import jakarta.validation.Valid;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.service.dto.ChangePasswordDto;
import org.scooterrental.service.dto.UserResponseDto;
import org.scooterrental.service.serviceinterface.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/scooter-rental/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/{userId}/change-username")
    @PreAuthorize("hasAuthority('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<UserResponseDto> changeUsername(@PathVariable("userId") Long userId, @RequestParam("newUsername") String newUsername) {
        return ResponseEntity.ok().body(userService.changeUsername(userId, newUsername));
    }

    @PatchMapping("/{userId}/change-password")
    @PreAuthorize("hasAuthority('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<String> changePassword(@PathVariable("userId") Long userId, @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(userId, changePasswordDto);
        return ResponseEntity.ok().body("Смена пароля успешна");
    }

    @PatchMapping("/{userId}/change-first-name")
    @PreAuthorize("hasAuthority('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<UserResponseDto> changeFirstName(@PathVariable("userId") Long userId, @RequestParam("newFirstName") String newFirstName) {
        return ResponseEntity.ok().body(userService.changeFirstName(userId, newFirstName));
    }

    @PatchMapping("/{userId}/change-last-name")
    @PreAuthorize("hasAuthority('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<UserResponseDto> changeLastName(@PathVariable("userId") Long userId, @RequestParam("newLastName") String newLastName) {
        return ResponseEntity.ok().body(userService.changeLastName(userId, newLastName));
    }

    @PatchMapping("/{userId}/change-age")
    @PreAuthorize("hasAuthority('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<UserResponseDto> changeAge(@PathVariable("userId") Long userId, @RequestParam("newAge") int newAge) {
        return ResponseEntity.ok().body(userService.changeAge(userId, newAge));
    }

    @PatchMapping("/{userId}/ban")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> banAccount(@PathVariable("userId") Long userId, @RequestParam("banReason") BanReason banReason) {
        return ResponseEntity.ok().body(userService.banAccount(userId, banReason));
    }

    @PatchMapping("/{userId}/unban")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> unbanAccount(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(userService.unbanAccount(userId));
    }

    @PatchMapping("/{userId}/add-money")
    @PreAuthorize("hasAuthority('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<UserResponseDto> addMoney(@PathVariable("userId") Long userId, @RequestParam("amount") BigDecimal amount) {
        return ResponseEntity.ok().body(userService.addMoney(userId, amount));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or authentication.principal.userId == #userId")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(userService.getUserById(userId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PatchMapping("/{userId}/set-admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> setAdmin(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(userService.setAdmin(userId));
    }

    @PatchMapping("/{userId}/buy-season-ticket")
    @PreAuthorize("hasAuthority('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<UserResponseDto> buySeasonTicket(@PathVariable("userId") Long userId, @RequestParam("monthsCount") int monthsCount) {
        return ResponseEntity.ok().body(userService.buySeasonTicket(userId, monthsCount));
    }
}
