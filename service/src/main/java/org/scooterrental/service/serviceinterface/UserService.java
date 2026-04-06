package org.scooterrental.service.serviceinterface;

import org.scooterrental.service.dto.UserCreateDto;
import org.scooterrental.service.dto.UserResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    UserResponseDto addNewUser(UserCreateDto userCreateDto);
    UserResponseDto changeUsername(Long userId, String newUsername);
    UserResponseDto changePassword(Long userId, String newPassword);
    UserResponseDto changeFirstName(Long userId, String newFirstName);
    UserResponseDto changeLastName(Long userId, String newLastName);
    UserResponseDto changeAge(Long userId, int newAge);
    UserResponseDto banAccount(Long userId);
    UserResponseDto unbanAccount(Long userId);
    UserResponseDto addMoney(Long userId, BigDecimal amount);
    UserResponseDto debitMoney(Long userId, BigDecimal amount);
    UserResponseDto getUserById(Long userId);
    List<UserResponseDto> getAllUsers();
    UserResponseDto setAdmin(Long userId);
    UserResponseDto buySeasonTicket(Long userId, int MonthsCount);
}
