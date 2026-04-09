package org.scooterrental.service.serviceimpl.security;

import org.scooterrental.model.entity.User;
import org.scooterrental.model.enums.Role;
import org.scooterrental.model.exception.UsernameAlreadyExistsException;
import org.scooterrental.model.exception.UsernameNotFoundException;
import org.scooterrental.repository.daointerface.UserDao;
import org.scooterrental.service.dto.AuthenticationResponseDto;
import org.scooterrental.service.dto.LoginRequest;
import org.scooterrental.service.dto.UserCreateDto;
import org.scooterrental.service.serviceinterface.security.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationResponseDto register(UserCreateDto userCreateDto) {
        String username = userCreateDto.getUsername();
        User user = userDao.findUserByUsername(username);
        if (user != null) {
            throw new UsernameAlreadyExistsException();
        }
        user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        user.setFirstName(userCreateDto.getFirstName());
        user.setLastName(userCreateDto.getLastName());
        user.setAge(userCreateDto.getAge());
        user.setRole(Role.ROLE_USER);
        userDao.create(user);
        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto();
        authenticationResponseDto.setToken(jwtService.generateToken(username));
        return authenticationResponseDto;
    }

    @Override
    public AuthenticationResponseDto login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto();
        authenticationResponseDto.setToken(jwtService.generateToken(loginRequest.getUsername()));
        return authenticationResponseDto;
    }
}
