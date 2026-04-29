package org.scooterrental.service.serviceimpl.security;

import lombok.RequiredArgsConstructor;
import org.scooterrental.model.entity.User;
import org.scooterrental.model.enums.Role;
import org.scooterrental.model.exception.UsernameAlreadyExistsException;
import org.scooterrental.repository.daointerface.UserDao;
import org.scooterrental.service.dto.AuthenticationResponseDto;
import org.scooterrental.service.dto.LoginRequest;
import org.scooterrental.service.dto.UserCreateDto;
import org.scooterrental.service.mapper.UserMapper;
import org.scooterrental.service.serviceinterface.security.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Override
    public AuthenticationResponseDto register(UserCreateDto userCreateDto) {
        String username = userCreateDto.getUsername();
        User user = userDao.findUserByUsername(username);
        if (user != null) {
            throw new UsernameAlreadyExistsException();
        }
        user = userMapper.toUserEntity(userCreateDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        userDao.create(user);
        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto();
        authenticationResponseDto.setToken(jwtService.generateToken(username));
        logger.info("Пользователь {} успешно зарегистрирован", username);
        return authenticationResponseDto;
    }

    @Override
    public AuthenticationResponseDto login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto();
        authenticationResponseDto.setToken(jwtService.generateToken(loginRequest.getUsername()));
        logger.info("Пользователь {} успешно авторизован", loginRequest.getUsername());
        return authenticationResponseDto;
    }
}
