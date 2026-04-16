package org.scooterrental.service.serviceimpl.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scooterrental.model.entity.User;
import org.scooterrental.model.exception.UsernameAlreadyExistsException;
import org.scooterrental.repository.daointerface.UserDao;
import org.scooterrental.service.dto.AuthenticationResponseDto;
import org.scooterrental.service.dto.LoginRequest;
import org.scooterrental.service.dto.UserCreateDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void register_ShouldReturnDto_WhenAllCorrect() {
        String username = "abc";
        String password = "abc";
        String token = "qwerty";

        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(username);
        userCreateDto.setPassword(password);
        userCreateDto.setAge(24);

        when(userDao.findUserByUsername(username)).thenReturn(null);
        when(jwtService.generateToken(username)).thenReturn("qwerty");
        when(passwordEncoder.encode(password)).thenReturn("asdfghjk");

        AuthenticationResponseDto actual = authenticationService.register(userCreateDto);

        assertNotNull(actual);
        assertEquals(token, actual.getToken());

        verify(userDao, times(1)).findUserByUsername(anyString());
        verify(userDao, times(1)).create(any(User.class));
    }

    @Test
    void register_ShouldThrowUsernameAlreadyExistsException_WhenUsernameAlreadyExists() {
        String username = "abc";
        String password = "abc";

        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(username);
        userCreateDto.setPassword(password);
        userCreateDto.setAge(24);

        User user = new User();
        user.setUsername(username);

        when(userDao.findUserByUsername(username)).thenReturn(user);

        assertThrows(UsernameAlreadyExistsException.class, () -> authenticationService.register(userCreateDto));

        verify(userDao, times(1)).findUserByUsername(anyString());
        verify(userDao, never()).create(any(User.class));
    }

    @Test
    void login_ShouldReturnDto_WhenAllCorrect() {
        String username = "abc";
        String password = "abc";
        String token = "qwerty";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);


        when(jwtService.generateToken(username)).thenReturn("qwerty");

        AuthenticationResponseDto actual = authenticationService.login(loginRequest);

        assertNotNull(actual);
        assertEquals(token, actual.getToken());

        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
    }

    @Test
    void login_ShouldThrowBadCredentialsException_WhenWrongUsernameOrPassword() {
        String username = "abc";
        String password = "abc";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(BadCredentialsException.class);

        assertThrows(BadCredentialsException.class, () -> authenticationService.login(loginRequest));

        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(jwtService, never()).generateToken(anyString());
    }
}
