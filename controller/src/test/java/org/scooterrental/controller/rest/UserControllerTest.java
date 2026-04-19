package org.scooterrental.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.scooterrental.controller.advice.GlobalExceptionHandler;
import org.scooterrental.model.entity.User;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.model.exception.UserNotBannedException;
import org.scooterrental.model.exception.UserAlreadyAdminException;
import org.scooterrental.model.exception.UsernameAlreadyExistsException;
import org.scooterrental.model.exception.UserNotFoundException;
import org.scooterrental.model.exception.ValueLessZeroException;
import org.scooterrental.model.exception.PasswordMismatchException;
import org.scooterrental.service.dto.ChangePasswordDto;
import org.scooterrental.service.dto.UserResponseDto;
import org.scooterrental.service.dto.UserUpdateDto;
import org.scooterrental.service.serviceinterface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {

    @SpringBootApplication
    @EnableMethodSecurity
    static class TestConfig {
    }

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void changeUserData_ShouldReturn200AndDto_WhenUserRequestsForSelfAndAllCorrect() throws Exception {
        Long userId = 1L;
        String body = """
                    {
                    "username": "newUsername",
                    "firstName": "Вася",
                    "lastName": "Иванов",
                    "age": 25
                    }
                """;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setUsername("newUsername");
        expected.setFirstName("Вася");
        expected.setLastName("Иванов");
        expected.setAge(25);

        when(userService.changeUserData(eq(userId), any(UserUpdateDto.class))).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-user-data", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.username").value("newUsername"))
                .andExpect(jsonPath("$.firstName").value("Вася"))
                .andExpect(jsonPath("$.lastName").value("Иванов"))
                .andExpect(jsonPath("$.age").value(25));

        verify(userService, times(1)).changeUserData(eq(userId), any(UserUpdateDto.class));
    }

    @Test
    void changeUserData_ShouldReturn403_WhenUserRequestsForOther() throws Exception {
        Long userId = 1L;
        Long otherUserId = 2L;
        String body = """
                    {
                    "username": "newUsername",
                    "firstName": "Вася",
                    "lastName": "Иванов",
                    "age": 25
                    }
                """;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-user-data", otherUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).changeUserData(anyLong(), any());
    }

    @Test
    void changeUserData_ShouldReturn403_WhenAdminRequestsForOther() throws Exception {
        Long adminId = 1L;
        Long otherUserId = 2L;
        String body = """
                    {
                    "username": "newUsername",
                    "firstName": "Вася",
                    "lastName": "Иванов",
                    "age": 25
                    }
                """;
        User customUser = new User();
        customUser.setUserId(adminId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-user-data", otherUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).changeUserData(anyLong(), any());
    }

    @Test
    void changeUserData_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long userId = 1L;
        String body = """
                    {
                    "username": "newUsername",
                    "firstName": "Вася",
                    "lastName": "Иванов",
                    "age": 25
                    }
                """;

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-user-data", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).changeUserData(anyLong(), any());
    }

    @Test
    void changeUserData_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 1L;
        String body = """
                    {
                    "username": "newUsername",
                    "firstName": "Вася",
                    "lastName": "Иванов",
                    "age": 25
                    }
                """;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(userService.changeUserData(eq(userId), any(UserUpdateDto.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-user-data", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("UserNotFoundException"));

        verify(userService, times(1)).changeUserData(eq(userId), any(UserUpdateDto.class));
    }

    @Test
    void changeUserData_ShouldReturn409_WhenUsernameAlreadyExists() throws Exception {
        Long userId = 1L;
        String body = """
                    {
                    "username": "newUsername",
                    "firstName": "Вася",
                    "lastName": "Иванов",
                    "age": 25
                    }
                """;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(userService.changeUserData(eq(userId), any(UserUpdateDto.class))).thenThrow(new UsernameAlreadyExistsException());

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-user-data", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("UsernameAlreadyExistsException"));

        verify(userService, times(1)).changeUserData(eq(userId), any(UserUpdateDto.class));
    }

    @Test
    void changeUserData_ShouldReturn400_WhenValueLessZero() throws Exception {
        Long userId = 1L;
        String body = """
                    {
                    "username": "newUsername",
                    "firstName": "Вася",
                    "lastName": "Иванов",
                    "age": 25
                    }
                """;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(userService.changeUserData(eq(userId), any(UserUpdateDto.class))).thenThrow(new ValueLessZeroException());

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-user-data", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ValueLessZeroException"));

        verify(userService, times(1)).changeUserData(eq(userId), any(UserUpdateDto.class));
    }

    @Test
    void changeUserData_ShouldReturn400_WhenMissingRequestBody() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-user-data", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("HttpMessageNotReadableException"));

        verify(userService, never()).changeUserData(anyLong(), any());
    }

    @Test
    void changePassword_ShouldReturn200AndString_WhenUserRequestsForSelfAndAllCorrect() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword("oldPassword");
        changePasswordDto.setNewPassword("newPassword123");

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-password", userId)
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Смена пароля успешна"));

        verify(userService, times(1)).changePassword(eq(userId), any(ChangePasswordDto.class));
    }

    @Test
    void changePassword_ShouldReturn403_WhenUserRequestsForOther() throws Exception {
        Long userId = 1L;
        Long otherUserId = 2L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword("oldPassword");
        changePasswordDto.setNewPassword("newPassword123");

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-password", otherUserId)
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).changePassword(anyLong(), any(ChangePasswordDto.class));
    }

    @Test
    void changePassword_ShouldReturn403_WhenAdminRequestsForOther() throws Exception {
        Long adminId = 1L;
        Long otherUserId = 2L;
        User customUser = new User();
        customUser.setUserId(adminId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword("oldPassword");
        changePasswordDto.setNewPassword("newPassword123");

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-password", otherUserId)
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).changePassword(anyLong(), any(ChangePasswordDto.class));
    }

    @Test
    void changePassword_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long userId = 1L;
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword("oldPassword");
        changePasswordDto.setNewPassword("newPassword123");

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-password", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDto)))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).changePassword(anyLong(), any(ChangePasswordDto.class));
    }

    @Test
    void changePassword_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword("oldPassword");
        changePasswordDto.setNewPassword("newPassword123");

        doThrow(new UserNotFoundException()).when(userService).changePassword(eq(userId), any(ChangePasswordDto.class));

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-password", userId)
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("UserNotFoundException"));

        verify(userService, times(1)).changePassword(eq(userId), any(ChangePasswordDto.class));
    }

    @Test
    void changePassword_ShouldReturn400_WhenPasswordMismatch() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword("oldPassword");
        changePasswordDto.setNewPassword("newPassword123");

        doThrow(new PasswordMismatchException()).when(userService).changePassword(eq(userId), any(ChangePasswordDto.class));

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-password", userId)
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("PasswordMismatchException"));

        verify(userService, times(1)).changePassword(eq(userId), any(ChangePasswordDto.class));
    }

    @Test
    void changePassword_ShouldReturn400_WhenMissingRequestBody() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/change-password", userId)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("HttpMessageNotReadableException"));

        verify(userService, never()).changePassword(anyLong(), any(ChangePasswordDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void banAccount_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long userId = 1L;
        BanReason banReason = BanReason.DEBT;

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setBanReason(banReason.toString());

        when(userService.banAccount(userId, banReason)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/ban", userId)
                        .param("banReason", banReason.toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.banReason").value(banReason.toString()));

        verify(userService, times(1)).banAccount(userId, banReason);
    }

    @Test
    @WithMockUser(roles = "USER")
    void banAccount_ShouldReturn403_WhenUserRequests() throws Exception {
        Long userId = 1L;
        BanReason banReason = BanReason.DEBT;

        mockMvc.perform(patch("/scooter-rental/users/{userId}/ban", userId)
                        .param("banReason", banReason.toString())
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).banAccount(anyLong(), any(BanReason.class));
    }

    @Test
    void banAccount_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long userId = 1L;
        BanReason banReason = BanReason.DEBT;

        mockMvc.perform(patch("/scooter-rental/users/{userId}/ban", userId)
                        .param("banReason", banReason.toString())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).banAccount(anyLong(), any(BanReason.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void banAccount_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 1L;
        BanReason banReason = BanReason.DEBT;

        when(userService.banAccount(userId, banReason)).thenThrow(new UserNotFoundException());

        mockMvc.perform(patch("/scooter-rental/users/{userId}/ban", userId)
                        .param("banReason", banReason.toString())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("UserNotFoundException"));

        verify(userService, times(1)).banAccount(userId, banReason);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void banAccount_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long userId = 1L;

        mockMvc.perform(patch("/scooter-rental/users/{userId}/ban", userId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(userService, never()).banAccount(anyLong(), any(BanReason.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void unbanAccount_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long userId = 1L;

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setBanReason(BanReason.NONE.toString());

        when(userService.unbanAccount(userId)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/unban", userId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.banReason").value(BanReason.NONE.toString()));

        verify(userService, times(1)).unbanAccount(userId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void unbanAccount_ShouldReturn403_WhenUserRequests() throws Exception {
        Long userId = 1L;

        mockMvc.perform(patch("/scooter-rental/users/{userId}/unban", userId)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).unbanAccount(anyLong());
    }

    @Test
    void unbanAccount_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long userId = 1L;

        mockMvc.perform(patch("/scooter-rental/users/{userId}/unban", userId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).unbanAccount(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void unbanAccount_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 1L;

        when(userService.unbanAccount(userId)).thenThrow(new UserNotFoundException());

        mockMvc.perform(patch("/scooter-rental/users/{userId}/unban", userId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("UserNotFoundException"));

        verify(userService, times(1)).unbanAccount(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void unbanAccount_ShouldReturn400_WhenUserNotBanned() throws Exception {
        Long userId = 1L;

        when(userService.unbanAccount(userId)).thenThrow(new UserNotBannedException());

        mockMvc.perform(patch("/scooter-rental/users/{userId}/unban", userId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("UserNotBannedException"));

        verify(userService, times(1)).unbanAccount(userId);
    }

    @Test
    void addMoney_ShouldReturn200AndDto_WhenUserRequestsForSelfAndAllCorrect() throws Exception {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);

        when(userService.addMoney(userId, amount)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/add-money", userId)
                        .param("amount", amount.toString())
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));

        verify(userService, times(1)).addMoney(userId, amount);
    }

    @Test
    void addMoney_ShouldReturn403_WhenUserRequestsForOther() throws Exception {
        Long userId = 1L;
        Long otherUserId = 2L;
        BigDecimal amount = new BigDecimal("100.00");
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/add-money", otherUserId)
                        .param("amount", amount.toString())
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).addMoney(anyLong(), any(BigDecimal.class));
    }

    @Test
    void addMoney_ShouldReturn403_WhenAdminRequests() throws Exception {
        Long adminId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        User customUser = new User();
        customUser.setUserId(adminId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/add-money", adminId)
                        .param("amount", amount.toString())
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).addMoney(anyLong(), any(BigDecimal.class));
    }

    @Test
    void addMoney_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("100.00");

        mockMvc.perform(patch("/scooter-rental/users/{userId}/add-money", userId)
                        .param("amount", amount.toString())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).addMoney(anyLong(), any(BigDecimal.class));
    }

    @Test
    void addMoney_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(userService.addMoney(userId, amount)).thenThrow(new UserNotFoundException());

        mockMvc.perform(patch("/scooter-rental/users/{userId}/add-money", userId)
                        .param("amount", amount.toString())
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("UserNotFoundException"));

        verify(userService, times(1)).addMoney(userId, amount);
    }

    @Test
    void addMoney_ShouldReturn400_WhenValueLessZero() throws Exception {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("-100.00");
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(userService.addMoney(userId, amount)).thenThrow(new ValueLessZeroException());

        mockMvc.perform(patch("/scooter-rental/users/{userId}/add-money", userId)
                        .param("amount", amount.toString())
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ValueLessZeroException"));

        verify(userService, times(1)).addMoney(userId, amount);
    }

    @Test
    void addMoney_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/add-money", userId)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(userService, never()).addMoney(anyLong(), any(BigDecimal.class));
    }

    @Test
    void getUser_ShouldReturn200AndDto_WhenUserRequestsForSelfAndAllCorrect() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);

        when(userService.getUserById(userId)).thenReturn(expected);

        mockMvc.perform(get("/scooter-rental/users/{userId}", userId)
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getUser_ShouldReturn403_WhenUserRequestsForOther() throws Exception {
        Long userId = 1L;
        Long otherUserId = 2L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(get("/scooter-rental/users/{userId}", otherUserId)
                        .with(authentication(auth)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUser_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long userId = 1L;
        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);

        when(userService.getUserById(userId)).thenReturn(expected);

        mockMvc.perform(get("/scooter-rental/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getUser_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/scooter-rental/users/{userId}", userId))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    void getUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/scooter-rental/users/{userId}", userId)
                        .with(authentication(auth)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("UserNotFoundException"));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturn200AndList_WhenAdminRequests() throws Exception {
        UserResponseDto dto1 = new UserResponseDto();
        dto1.setUserId(1L);

        UserResponseDto dto2 = new UserResponseDto();
        dto2.setUserId(2L);

        when(userService.getAllUsers()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/scooter-rental/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[1].userId").value(2L));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_ShouldReturn403_WhenUserRequests() throws Exception {
        mockMvc.perform(get("/scooter-rental/users"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).getAllUsers();
    }

    @Test
    void getAllUsers_ShouldReturn401_WhenNonameRequests() throws Exception {
        mockMvc.perform(get("/scooter-rental/users"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setAdmin_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long userId = 1L;
        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);

        when(userService.setAdmin(userId)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/set-admin", userId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));

        verify(userService, times(1)).setAdmin(userId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void setAdmin_ShouldReturn403_WhenUserRequests() throws Exception {
        Long userId = 1L;

        mockMvc.perform(patch("/scooter-rental/users/{userId}/set-admin", userId)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).setAdmin(anyLong());
    }

    @Test
    void setAdmin_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long userId = 1L;

        mockMvc.perform(patch("/scooter-rental/users/{userId}/set-admin", userId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).setAdmin(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setAdmin_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 1L;

        when(userService.setAdmin(userId)).thenThrow(new UserNotFoundException());

        mockMvc.perform(patch("/scooter-rental/users/{userId}/set-admin", userId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("UserNotFoundException"));

        verify(userService, times(1)).setAdmin(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setAdmin_ShouldReturn409_WhenUserAlreadyAdmin() throws Exception {
        Long userId = 1L;

        when(userService.setAdmin(userId)).thenThrow(new UserAlreadyAdminException());

        mockMvc.perform(patch("/scooter-rental/users/{userId}/set-admin", userId)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("UserAlreadyAdminException"));

        verify(userService, times(1)).setAdmin(userId);
    }

    @Test
    void buySeasonTicket_ShouldReturn200AndDto_WhenUserRequestsForSelfAndAllCorrect() throws Exception {
        Long userId = 1L;
        int monthsCount = 3;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);

        when(userService.buySeasonTicket(userId, monthsCount)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/buy-season-ticket", userId)
                        .param("monthsCount", String.valueOf(monthsCount))
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));

        verify(userService, times(1)).buySeasonTicket(userId, monthsCount);
    }

    @Test
    void buySeasonTicket_ShouldReturn403_WhenUserRequestsForOther() throws Exception {
        Long userId = 1L;
        Long otherUserId = 2L;
        int monthsCount = 3;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/buy-season-ticket", otherUserId)
                        .param("monthsCount", String.valueOf(monthsCount))
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).buySeasonTicket(anyLong(), anyInt());
    }

    @Test
    void buySeasonTicket_ShouldReturn403_WhenAdminRequests() throws Exception {
        Long adminId = 1L;
        int monthsCount = 3;
        User customUser = new User();
        customUser.setUserId(adminId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/buy-season-ticket", adminId)
                        .param("monthsCount", String.valueOf(monthsCount))
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(userService, never()).buySeasonTicket(anyLong(), anyInt());
    }

    @Test
    void buySeasonTicket_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long userId = 1L;
        int monthsCount = 3;

        mockMvc.perform(patch("/scooter-rental/users/{userId}/buy-season-ticket", userId)
                        .param("monthsCount", String.valueOf(monthsCount))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).buySeasonTicket(anyLong(), anyInt());
    }

    @Test
    void buySeasonTicket_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 1L;
        int monthsCount = 3;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(userService.buySeasonTicket(userId, monthsCount)).thenThrow(new UserNotFoundException());

        mockMvc.perform(patch("/scooter-rental/users/{userId}/buy-season-ticket", userId)
                        .param("monthsCount", String.valueOf(monthsCount))
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("UserNotFoundException"));

        verify(userService, times(1)).buySeasonTicket(userId, monthsCount);
    }

    @Test
    void buySeasonTicket_ShouldReturn400_WhenWrongMonthsCount() throws Exception {
        Long userId = 1L;
        int monthsCount = 15;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(userService.buySeasonTicket(userId, monthsCount)).thenThrow(new IllegalArgumentException("Количество месяцев в абонементе может быть от 1 до 12"));

        mockMvc.perform(patch("/scooter-rental/users/{userId}/buy-season-ticket", userId)
                        .param("monthsCount", String.valueOf(monthsCount))
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("IllegalArgumentException"));

        verify(userService, times(1)).buySeasonTicket(userId, monthsCount);
    }

    @Test
    void buySeasonTicket_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(patch("/scooter-rental/users/{userId}/buy-season-ticket", userId)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(userService, never()).buySeasonTicket(anyLong(), anyInt());
    }
}
