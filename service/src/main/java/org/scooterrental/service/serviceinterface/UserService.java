package org.scooterrental.service.serviceinterface;

import org.scooterrental.service.dto.UserCreateDto;
import org.scooterrental.service.dto.UserResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    UserResponseDto addNewUser(UserCreateDto userCreateDto);
    UserResponseDto addMoney(Long userId, BigDecimal amount);
    UserResponseDto debitMoney(Long userId, BigDecimal amount);
    UserResponseDto getUser(Long userId);
    List<UserResponseDto> getAllUsers();
}
