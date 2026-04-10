package org.scooterrental.controller.advice;

import org.scooterrental.model.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<Map<String, String>> buildResponse(HttpStatus httpStatus, String message, Exception e) {
        Map<String, String> response = new LinkedHashMap<>();
        String errorMessage;
        if (message == null) {
            errorMessage = e.getClass().getSimpleName();
        } else {
            errorMessage = message;
        }
        response.put("type", e.getClass().getSimpleName());
        response.put("message", errorMessage);
        logger.error("Ошибка: {}", errorMessage, e);
        return ResponseEntity.status(httpStatus).body(response);
    }

    private ResponseEntity<Map<String, String>> buildResponse(Map<String, String> errors) {
        logger.error("Ошибка: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleNotValidArgumentException(MethodArgumentNotValidException e) {
        Map<String, String> map = new LinkedHashMap<>();
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        for (ObjectError error : errors) {
            FieldError fieldError = (FieldError) error;
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return buildResponse(map);
    }

    @ExceptionHandler({RentalPointNotFoundException.class,
            ScooterNotFoundException.class,
            TariffNotFoundException.class,
            TripNotFoundException.class,
            UserNotFoundException.class,
            NoResourceFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(Exception e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), e);
    }

    @ExceptionHandler({ScooterAlreadyAvailableException.class,
            ScooterAlreadyInRentException.class,
            ScooterInServiceException.class,
            ScooterInWarehouseException.class,
            UsernameAlreadyExistsException.class,
            UserAlreadyAdminException.class,
            RentalPointAlreadyExistsException.class,
            SameRentalPointsIDException.class,
            RentalPointNotEmptyException.class,
            ScooterNotAvailableException.class,
            TripAlreadyCompletedException.class,
            UserHasNoActiveSeasonTicketException.class,
            UserAlreadyHasActiveTripException.class})
    public ResponseEntity<Map<String, String>> handleConflictException(RuntimeException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage(), e);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenException(AccessDeniedException e) {
        return buildResponse(HttpStatus.FORBIDDEN, "К этому сайту у вас нет прав доступа", e);
    }

    @ExceptionHandler(UserBannedException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenException(RuntimeException e) {
        return buildResponse(HttpStatus.FORBIDDEN, e.getMessage(), e);
    }

    @ExceptionHandler({UsernameNotFoundException.class, WrongPasswordException.class, BadCredentialsException.class})
    public ResponseEntity<Map<String, String>> handleUnauthorizedException(RuntimeException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Неверный логин или пароль", e);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Запрос " + e.getMethod() + " не поддерживается для этого адреса", e);
    }

    @ExceptionHandler({ValueLessZeroException.class,
            IllegalArgumentException.class,
            PasswordMismatchException.class,
            MethodArgumentTypeMismatchException.class,
            LowBatteryLevelException.class})
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(RuntimeException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOtherException(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка", e);
    }
}
