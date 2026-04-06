package org.scooterrental.service.serviceimpl;

import org.scooterrental.model.entity.User;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.model.enums.Role;
import org.scooterrental.model.exception.*;
import org.springframework.transaction.annotation.Transactional;
import org.scooterrental.repository.daointerface.UserDao;
import org.scooterrental.service.dto.UserCreateDto;
import org.scooterrental.service.dto.UserResponseDto;
import org.scooterrental.service.mapper.UserMapper;
import org.scooterrental.service.serviceinterface.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;

    public UserServiceImpl(UserDao userDao, UserMapper userMapper) {
        this.userDao = userDao;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDto addNewUser(UserCreateDto userCreateDto) {
        String username = userCreateDto.getUsername();
        User user = userDao.findUserByUsername(username);
        if (user != null) {
            throw new UsernameAlreadyExistsException();
        }
        user = userMapper.toUserEntity(userCreateDto);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto changeUsername(Long userId, String newUsername) {
        User user = getUserOrThrow(userId);
        User userCheck = userDao.findUserByUsername(newUsername);
        if (userCheck != null && !user.getUserId().equals(userCheck.getUserId())) {
            throw new UsernameAlreadyExistsException();
        }
        user.setUsername(newUsername);
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto changePassword(Long userId, String newPassword) {
        return null;
    }

    @Override
    public UserResponseDto changeFirstName(Long userId, String newFirstName) {
        User user = getUserOrThrow(userId);
        user.setFirstName(newFirstName);
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto changeLastName(Long userId, String newLastName) {
        User user = getUserOrThrow(userId);
        user.setLastName(newLastName);
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto changeAge(Long userId, int newAge) {
        User user = getUserOrThrow(userId);
        if (newAge < 0) {
            throw new ValueLessZeroException();
        }
        user.setAge(newAge);
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto banAccount(Long userId, BanReason banReason) {
        User user = getUserOrThrow(userId);
        user.setBanReason(BanReason.DEBT);
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto unbanAccount(Long userId) {
        User user = getUserOrThrow(userId);
        user.setBanReason(BanReason.NONE);
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto addMoney(Long userId, BigDecimal amount) {
        User user = getUserOrThrow(userId);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValueLessZeroException();
        }
        user.setBalance(user.getBalance().add(amount));
        if (user.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
            if (user.getBanReason() == BanReason.DEBT) {
                user.setBanReason(BanReason.NONE);
            }
        }
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto debitMoney(Long userId, BigDecimal amount) {
        User user = getUserOrThrow(userId);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValueLessZeroException();
        }
        BigDecimal currentUserBalance = user.getBalance();
        if (currentUserBalance.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            user.setBanReason(BanReason.DEBT);
        }
        user.setBalance(currentUserBalance.subtract(amount));
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = getUserOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userDao.findUsers().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserResponseDto setAdmin(Long userId) {
        User user = getUserOrThrow(userId);
        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new UserAlreadyAdminException();
        }
        user.setRole(Role.ROLE_ADMIN);
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto buySeasonTicket(Long userId, int monthsCount) {
        User user = getUserOrThrow(userId);
        if (monthsCount < 0 || monthsCount > 12) {
            throw new IllegalArgumentException("Количество месяцев в абонементе может быть от 1 до 12");
        }
        LocalDateTime endDate;
        if (user.getSeasonTicketEndDate() != null) {
            endDate = user.getSeasonTicketEndDate().plusMonths(monthsCount);
        } else {
            endDate = LocalDateTime.now().plusMonths(monthsCount);
        }
        user.setSeasonTicketEndDate(endDate);
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    private User getUserOrThrow(Long userId) {
        User user = userDao.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }
}
