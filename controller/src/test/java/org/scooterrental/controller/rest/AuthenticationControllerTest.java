package org.scooterrental.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.scooterrental.controller.advice.GlobalExceptionHandler;
import org.scooterrental.model.exception.UsernameAlreadyExistsException;
import org.scooterrental.service.dto.AuthenticationResponseDto;
import org.scooterrental.service.dto.LoginRequest;
import org.scooterrental.service.dto.UserCreateDto;
import org.scooterrental.service.serviceinterface.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {
    @SpringBootApplication
    @EnableMethodSecurity
    static class TestConfig {
    }

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void register_ShouldReturn200AndDto_WhenRequestIsCorrect() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("testUser");
        userCreateDto.setPassword("password");
        userCreateDto.setFirstName("Иван");
        userCreateDto.setLastName("Иванов");
        userCreateDto.setAge(25);

        AuthenticationResponseDto expected = new AuthenticationResponseDto();
        expected.setToken("jwt.token.here");

        when(authenticationService.register(any(UserCreateDto.class))).thenReturn(expected);

        mockMvc.perform(post("/scooter-rental/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"));

        verify(authenticationService, times(1)).register(any(UserCreateDto.class));
    }

    @Test
    void register_ShouldReturn409_WhenUsernameAlreadyExists() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("testUser");
        userCreateDto.setPassword("password");
        userCreateDto.setFirstName("Иван");
        userCreateDto.setLastName("Иванов");
        userCreateDto.setAge(25);

        when(authenticationService.register(any(UserCreateDto.class))).thenThrow(new UsernameAlreadyExistsException());

        mockMvc.perform(post("/scooter-rental/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("UsernameAlreadyExistsException"));

        verify(authenticationService, times(1)).register(any(UserCreateDto.class));
    }

    @Test
    void register_ShouldReturn400_WhenMissingRequestBody() throws Exception {
        mockMvc.perform(post("/scooter-rental/auth/register")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).register(any(UserCreateDto.class));
    }

    @Test
    void login_ShouldReturn200AndDto_WhenRequestIsCorrect() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("password");

        AuthenticationResponseDto expected = new AuthenticationResponseDto();
        expected.setToken("jwt.token.here");

        when(authenticationService.login(any(LoginRequest.class))).thenReturn(expected);

        mockMvc.perform(post("/scooter-rental/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"));

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_ShouldReturn401_WhenBadCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("wrongPassword");

        when(authenticationService.login(any(LoginRequest.class))).thenThrow(new BadCredentialsException("Неверный логин или пароль"));

        mockMvc.perform(post("/scooter-rental/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type").value("BadCredentialsException"));

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_ShouldReturn400_WhenMissingRequestBody() throws Exception {
        mockMvc.perform(post("/scooter-rental/auth/login")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).login(any(LoginRequest.class));
    }
}
