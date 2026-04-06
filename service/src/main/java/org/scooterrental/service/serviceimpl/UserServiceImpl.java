package org.scooterrental.service.serviceimpl;

import org.scooterrental.model.entity.User;
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
    public UserResponseDto addMoney(Long userId, BigDecimal amount) {
        User user = userDao.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValueLessZeroException();
        }
        user.setBalance(user.getBalance().add(amount));
        if (user.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
            user.setBanned(false);
        }
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto debitMoney(Long userId, BigDecimal amount) {
        User user = userDao.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValueLessZeroException();
        }
        BigDecimal currentUserBalance = user.getBalance();
        if (currentUserBalance.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            user.setBanned(true);
        }
        user.setBalance(currentUserBalance.subtract(amount));
        userDao.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = userDao.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
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
        User user = userDao.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new UserAlreadyAdminException();
        }
        user.setRole(Role.ROLE_ADMIN);
        userDao.update(user);
        return userMapper.toUserDto(user);
    }
}
