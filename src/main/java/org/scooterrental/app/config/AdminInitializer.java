package org.scooterrental.app.config;

import org.scooterrental.model.entity.User;
import org.scooterrental.model.enums.Role;
import org.scooterrental.repository.daointerface.UserDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminInitializer(UserDao userDao, PasswordEncoder passwordEncoder,
                            @Value("${app.admin.username}") String adminUsername,
                            @Value("${app.admin.password}") String adminPassword) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<User> users = userDao.findUsers();
        if (!users.isEmpty()) {
            return;
        }
        if (adminUsername.isBlank() || adminPassword.isBlank()) {
            throw new IllegalStateException("Укажите ADMIN_USERNAME и ADMIN_PASSWORD в .env");
        }
        User user = new User();
        user.setUsername(adminUsername);
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setAge(1);
        user.setPassword(passwordEncoder.encode(adminPassword));
        user.setRole(Role.ROLE_ADMIN);
        userDao.create(user);
    }
}
