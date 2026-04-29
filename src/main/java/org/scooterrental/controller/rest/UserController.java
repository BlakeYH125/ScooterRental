package org.scooterrental.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.service.dto.ChangePasswordDto;
import org.scooterrental.service.dto.UserResponseDto;
import org.scooterrental.service.dto.UserUpdateDto;
import org.scooterrental.service.serviceinterface.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scooter-rental/users")
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PatchMapping("/{userId}/change-user-data")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN') and authentication.principal.userId == #userId")
    public ResponseEntity<UserResponseDto> changeUserData(@PathVariable("userId") Long userId, @RequestBody UserUpdateDto userUpdateDto) {
        logger.info("Получен запрос на изменение личных данных пользователя {}", userId);
        return ResponseEntity.ok().body(userService.changeUserData(userId, userUpdateDto));
    }

    @PatchMapping("/{userId}/change-password")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN') and authentication.principal.userId == #userId")
    public ResponseEntity<String> changePassword(@PathVariable("userId") Long userId, @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        logger.info("Получен запрос на изменение пароль у пользователя {}", userId);
        userService.changePassword(userId, changePasswordDto);
        return ResponseEntity.ok().body("Смена пароля успешна");
    }

    @PatchMapping("/{userId}/ban")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> banAccount(@PathVariable("userId") Long userId, @RequestParam("banReason") BanReason banReason) {
        logger.info("Получен запрос на блокировку пользователя {}", userId);
        return ResponseEntity.ok().body(userService.banAccount(userId, banReason));
    }

    @PatchMapping("/{userId}/unban")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> unbanAccount(@PathVariable("userId") Long userId) {
        logger.info("Получен запрос на разблокировку пользователя {}", userId);
        return ResponseEntity.ok().body(userService.unbanAccount(userId));
    }

    @PatchMapping("/{userId}/add-money")
    @PreAuthorize("hasAuthority('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<UserResponseDto> addMoney(@PathVariable("userId") Long userId, @RequestParam("amount") BigDecimal amount) {
        logger.info("Получен запрос на зачисление денег на баланс пользователя {}", userId);
        return ResponseEntity.ok().body(userService.addMoney(userId, amount));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or authentication.principal.userId == #userId")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("userId") Long userId) {
        logger.info("Получен запрос на информацию о пользователе {}", userId);
        return ResponseEntity.ok().body(userService.getUserById(userId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        logger.info("Получен запрос на информацию о всех пользователях");
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PatchMapping("/{userId}/set-admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> setAdmin(@PathVariable("userId") Long userId) {
        logger.info("Получен запрос на назначение администратором пользователя {}", userId);
        return ResponseEntity.ok().body(userService.setAdmin(userId));
    }

    @PatchMapping("/{userId}/buy-season-ticket")
    @PreAuthorize("hasAuthority('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<UserResponseDto> buySeasonTicket(@PathVariable("userId") Long userId, @RequestParam("monthsCount") int monthsCount) {
        logger.info("Получен запрос на покупку абонемента пользователем {}", userId);
        return ResponseEntity.ok().body(userService.buySeasonTicket(userId, monthsCount));
    }
}
