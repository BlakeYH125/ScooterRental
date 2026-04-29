package org.scooterrental.service.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.scooterrental.model.entity.User;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.model.enums.Role;
import org.scooterrental.model.exception.UserNotBannedException;
import org.scooterrental.model.exception.UserAlreadyAdminException;
import org.scooterrental.model.exception.UsernameAlreadyExistsException;
import org.scooterrental.model.exception.UserNotFoundException;
import org.scooterrental.model.exception.ValueLessZeroException;
import org.scooterrental.model.exception.PasswordMismatchException;
import org.scooterrental.service.dto.ChangePasswordDto;
import org.scooterrental.service.dto.UserUpdateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.scooterrental.repository.daointerface.UserDao;
import org.scooterrental.service.dto.UserResponseDto;
import org.scooterrental.service.mapper.UserMapper;
import org.scooterrental.service.serviceinterface.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserResponseDto changeUserData(Long userId, UserUpdateDto userUpdateDto) {
        User user = getUserOrThrow(userId);
        String newUsername = userUpdateDto.getUsername();
        String newFirstName = userUpdateDto.getFirstName();
        String newLastName = userUpdateDto.getLastName();
        Integer newAge = userUpdateDto.getAge();
        if (newUsername != null) {
            User userCheck = userDao.findUserByUsername(newUsername);
            if (userCheck != null && !user.getUserId().equals(userCheck.getUserId())) {
                throw new UsernameAlreadyExistsException();
            }
            user.setUsername(newUsername);
            logger.info("Пользователь {} успешно изменил имя пользователя", userId);
        }
        if (newFirstName != null) {
            user.setFirstName(newFirstName);
            logger.info("Пользователь {} успешно изменил имя", userId);
        }
        if (newLastName != null) {
            user.setLastName(newLastName);
            logger.info("Пользователь {} успешно изменил фамилию", userId);
        }
        if (newAge != null) {
            if (newAge < 0) {
                throw new ValueLessZeroException();
            }
            user.setAge(newAge);
            logger.info("Пользователь {} успешно изменил возраст", userId);
        }
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDto changePasswordDto) {
        User user = getUserOrThrow(userId);
        String currentPasswordCode = user.getPassword();
        String currentPasswordFromDto = changePasswordDto.getOldPassword();
        if (passwordEncoder.matches(currentPasswordFromDto, currentPasswordCode)) {
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            userDao.update(user);
            logger.info("Пользователь {} успешно изменил пароль", userId);
        } else {
            throw new PasswordMismatchException();
        }
    }

    @Override
    public UserResponseDto banAccount(Long userId, BanReason banReason) {
        User user = getUserOrThrow(userId);
        user.setBanReason(banReason);
        userDao.update(user);
        logger.info("Пользователь {} был заблокирован по причине {}", userId, banReason);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto unbanAccount(Long userId) {
        User user = getUserOrThrow(userId);
        if (user.getBanReason() == BanReason.NONE) {
            throw new UserNotBannedException();
        }
        user.setBanReason(BanReason.NONE);
        userDao.update(user);
        logger.info("Пользователь {} был разблокирован", userId);
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
        logger.info("Баланс пользователя {} успешно пополнен", userId);
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
        logger.info("С баланса пользователя {} успешно списаны средства", userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = getUserOrThrow(userId);
        logger.info("Пользователь {} успешно запрошен", userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<UserResponseDto> list = userDao.findUsers().stream()
                .map(userMapper::toUserDto)
                .toList();
        logger.info("Успешно запрошен список всех пользователей");
        return list;
    }

    @Override
    public UserResponseDto setAdmin(Long userId) {
        User user = getUserOrThrow(userId);
        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new UserAlreadyAdminException();
        }
        user.setRole(Role.ROLE_ADMIN);
        userDao.update(user);
        logger.info("Пользователь {} успешно назначен администратором", userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto buySeasonTicket(Long userId, int monthsCount) {
        User user = getUserOrThrow(userId);
        if (monthsCount < 1 || monthsCount > 12) {
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
        logger.info("Пользователь {} успешно приобрел абонемент", userId);
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
