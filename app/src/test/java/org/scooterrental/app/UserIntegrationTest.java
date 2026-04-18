package org.scooterrental.app;

import org.junit.jupiter.api.Test;
import org.scooterrental.model.entity.User;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.model.enums.Role;
import org.scooterrental.repository.daointerface.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class UserIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldChangeUsername() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/change-username")
                        .param("newUsername", "newUsername123")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newUsername123"));
    }

    @Test
    void shouldThrowForbiddenWhenChangeUsernameOfAnotherUser() throws Exception {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword(passwordEncoder.encode("password"));
        user1.setFirstName("Test");
        user1.setLastName("User");
        user1.setAge(20);
        user1.setBalance(new BigDecimal(1000));
        user1.setBanReason(BanReason.NONE);
        user1.setRole(Role.ROLE_USER);
        userDao.create(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("password"));
        user2.setFirstName("Test");
        user2.setLastName("User");
        user2.setAge(20);
        user2.setBalance(new BigDecimal(1000));
        user2.setBanReason(BanReason.NONE);
        user2.setRole(Role.ROLE_USER);
        userDao.create(user2);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user2, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/scooter-rental/users/" + user1.getUserId() + "/change-username")
                        .param("newUsername", "hacked")
                        .with(authentication(auth)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));
    }

    @Test
    void shouldThrowUsernameAlreadyExistsException() throws Exception {
        User user1 = new User();
        user1.setUsername("existing");
        user1.setPassword(passwordEncoder.encode("password"));
        user1.setFirstName("Test");
        user1.setLastName("User");
        user1.setAge(20);
        user1.setBalance(new BigDecimal(1000));
        user1.setBanReason(BanReason.NONE);
        user1.setRole(Role.ROLE_USER);
        userDao.create(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("password"));
        user2.setFirstName("Test");
        user2.setLastName("User");
        user2.setAge(20);
        user2.setBalance(new BigDecimal(1000));
        user2.setBanReason(BanReason.NONE);
        user2.setRole(Role.ROLE_USER);
        userDao.create(user2);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user2, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/scooter-rental/users/" + user2.getUserId() + "/change-username")
                        .param("newUsername", "existing")
                        .with(authentication(auth)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("UsernameAlreadyExistsException"));
    }

    @Test
    void shouldChangePassword() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("oldPassword"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String body = """
                {
                    "oldPassword": "oldPassword",
                    "newPassword": "newPassword123"
                }
                """;

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(content().string("Смена пароля успешна"));
    }

    @Test
    void shouldThrowPasswordMismatchException() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("oldPassword"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String body = """
                {
                    "oldPassword": "wrongPassword",
                    "newPassword": "newPassword123"
                }
                """;

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("PasswordMismatchException"));
    }

    @Test
    void shouldChangeFirstName() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/change-first-name")
                        .param("newFirstName", "Ivan")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ivan"));
    }

    @Test
    void shouldChangeLastName() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/change-last-name")
                        .param("newLastName", "Ivanov")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Ivanov"));
    }

    @Test
    void shouldChangeAge() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/change-age")
                        .param("newAge", "25")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void shouldThrowValueLessZeroExceptionWhenChangeAge() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/change-age")
                        .param("newAge", "-5")
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ValueLessZeroException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldBanAccount() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/ban")
                        .param("banReason", BanReason.DEBT.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.banReason").value("DEBT"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldThrowForbiddenWhenBanAccount() throws Exception {
        mockMvc.perform(patch("/scooter-rental/users/1/ban")
                        .param("banReason", BanReason.VANDALISM.name()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUnbanAccount() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.VANDALISM);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/unban"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.banReason").value("NONE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowUserNotBannedException() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/unban"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("UserNotBannedException"));
    }

    @Test
    void shouldAddMoney() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/add-money")
                        .param("amount", "500")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    void shouldThrowValueLessZeroExceptionWhenAddMoney() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/add-money")
                        .param("amount", "-500")
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ValueLessZeroException"));
    }

    @Test
    void shouldGetUser() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(get("/scooter-rental/users/" + user.getUserId())
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void shouldThrowUnauthorized() throws Exception {
        mockMvc.perform(get("/scooter-rental/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsers() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        mockMvc.perform(get("/scooter-rental/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldThrowForbiddenWhenGetAllUsers() throws Exception {
        mockMvc.perform(get("/scooter-rental/users"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldSetAdmin() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/set-admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowUserAlreadyAdminException() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_ADMIN);
        userDao.create(user);

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/set-admin"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("UserAlreadyAdminException"));
    }

    @Test
    void shouldBuySeasonTicket() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/scooter-rental/users/" + user.getUserId() + "/buy-season-ticket")
                        .param("monthsCount", "3")
                        .with(authentication(auth)))
                .andExpect(status().isOk());
    }
}