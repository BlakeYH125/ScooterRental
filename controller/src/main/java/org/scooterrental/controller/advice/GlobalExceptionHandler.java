package org.scooterrental.controller.advice;

import org.scooterrental.model.exception.RentalPointNotFoundException;
import org.scooterrental.model.exception.RentalPointNotEmptyException;
import org.scooterrental.model.exception.RentalPointAlreadyExistsException;
import org.scooterrental.model.exception.SameRentalPointsIDException;
import org.scooterrental.model.exception.ScooterNotFoundException;
import org.scooterrental.model.exception.ScooterNotAvailableException;
import org.scooterrental.model.exception.ScooterAlreadyInRentException;
import org.scooterrental.model.exception.ScooterInWarehouseException;
import org.scooterrental.model.exception.ScooterInServiceException;
import org.scooterrental.model.exception.ScooterAlreadyAvailableException;
import org.scooterrental.model.exception.UserNotBannedException;
import org.scooterrental.model.exception.UserAlreadyAdminException;
import org.scooterrental.model.exception.UserBannedException;
import org.scooterrental.model.exception.UsernameAlreadyExistsException;
import org.scooterrental.model.exception.UserAlreadyHasActiveTripException;
import org.scooterrental.model.exception.UsernameNotFoundException;
import org.scooterrental.model.exception.UserHasNoActiveSeasonTicketException;
import org.scooterrental.model.exception.UserNotFoundException;
import org.scooterrental.model.exception.TariffNotFoundException;
import org.scooterrental.model.exception.TripAlreadyCompletedException;
import org.scooterrental.model.exception.TripNotFoundException;
import org.scooterrental.model.exception.WrongPasswordException;
import org.scooterrental.model.exception.ValueLessZeroException;
import org.scooterrental.model.exception.PasswordMismatchException;
import org.scooterrental.model.exception.LowBatteryLevelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    private ResponseEntity<Map<String, String>> buildResponse(Map<String, String> response) {
        logger.error("Ошибка: {}", response);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleNotValidArgumentException(MethodArgumentNotValidException e) {
        String combinedMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        Map<String, String> response = new LinkedHashMap<>();
        response.put("type", e.getClass().getSimpleName());
        response.put("message", combinedMessage);
        return buildResponse(response);
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
            LowBatteryLevelException.class,
            UserNotBannedException.class})
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(RuntimeException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParamsException(Exception e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Не указан параметр для этого адреса", e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(Exception e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Отсутствует или некорректно сформировано тело запроса", e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOtherException(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка", e);
    }
}
