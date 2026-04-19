package org.scooterrental.app;

import org.junit.jupiter.api.Test;
import org.scooterrental.model.entity.User;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.model.enums.Role;
import org.scooterrental.repository.daointerface.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUser() throws Exception {
        String body = """
                {
                    "username": "newuser",
                    "password": "password123",
                    "firstName": "Ivan",
                    "lastName": "Ivanov",
                    "age": 25
                }
                """;
        mockMvc.perform(post("/scooter-rental/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldThrowArgumentNotValidExceptionWhenRegister() throws Exception {
        String body = "{}";
        mockMvc.perform(post("/scooter-rental/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MethodArgumentNotValidException"));
    }

    @Test
    void shouldThrowUsernameAlreadyExistsExceptionWhenRegister() throws Exception {
        User user = new User();
        user.setUsername("existinguser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        String body = """
                {
                    "username": "existinguser",
                    "password": "password123",
                    "firstName": "Ivan",
                    "lastName": "Ivanov",
                    "age": 25
                }
                """;
        mockMvc.perform(post("/scooter-rental/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("UsernameAlreadyExistsException"));
    }

    @Test
    void shouldLoginUser() throws Exception {
        User user = new User();
        user.setUsername("loginuser");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        String body = """
                {
                    "username": "loginuser",
                    "password": "password123"
                }
                """;
        mockMvc.perform(post("/scooter-rental/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldThrowArgumentNotValidExceptionWhenLogin() throws Exception {
        String body = "{}";
        mockMvc.perform(post("/scooter-rental/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MethodArgumentNotValidException"));
    }

    @Test
    void shouldThrowUnauthorizedWhenLoginWithBadCredentials() throws Exception {
        User user = new User();
        user.setUsername("loginuser");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        String body = """
                {
                    "username": "loginuser",
                    "password": "wrongpassword"
                }
                """;
        mockMvc.perform(post("/scooter-rental/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}