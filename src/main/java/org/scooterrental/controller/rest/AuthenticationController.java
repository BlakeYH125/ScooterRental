package org.scooterrental.controller.rest;

import jakarta.validation.Valid;
import org.scooterrental.service.dto.AuthenticationResponseDto;
import org.scooterrental.service.dto.LoginRequest;
import org.scooterrental.service.dto.UserCreateDto;
import org.scooterrental.service.serviceinterface.security.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("scooter-rental/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(@Valid @RequestBody UserCreateDto userCreateDto) {
        logger.info("Получен запрос на регистрацию нового пользователя");
        return ResponseEntity.ok().body(authenticationService.register(userCreateDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Получен запрос на авторизацию нового пользователя");
        return ResponseEntity.ok().body(authenticationService.login(loginRequest));
    }
}
