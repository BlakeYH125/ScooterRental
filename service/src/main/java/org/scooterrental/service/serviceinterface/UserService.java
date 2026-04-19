package org.scooterrental.service.serviceinterface;

import org.scooterrental.model.enums.BanReason;
import org.scooterrental.service.dto.ChangePasswordDto;
import org.scooterrental.service.dto.UserResponseDto;
import org.scooterrental.service.dto.UserUpdateDto;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    UserResponseDto changeUserData(Long userId, UserUpdateDto userUpdateDto);
    void changePassword(Long userId, ChangePasswordDto changePasswordDto);
    UserResponseDto banAccount(Long userId, BanReason banReason);
    UserResponseDto unbanAccount(Long userId);
    UserResponseDto addMoney(Long userId, BigDecimal amount);
    UserResponseDto debitMoney(Long userId, BigDecimal amount);
    UserResponseDto getUserById(Long userId);
    List<UserResponseDto> getAllUsers();
    UserResponseDto setAdmin(Long userId);
    UserResponseDto buySeasonTicket(Long userId, int MonthsCount);
}
