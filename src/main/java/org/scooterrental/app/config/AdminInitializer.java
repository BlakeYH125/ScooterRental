package org.scooterrental.app.config;

import org.scooterrental.model.entity.User;
import org.scooterrental.model.enums.Role;
import org.scooterrental.repository.daointerface.UserDao;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<User> users = userDao.findUsers();
        if (users.isEmpty()) {
            User user = new User();
            user.setUsername("admin");
            user.setFirstName("admin");
            user.setLastName("admin");
            user.setAge(1);
            user.setPassword(passwordEncoder.encode("admin"));
            user.setRole(Role.ROLE_ADMIN);
            userDao.create(user);
        }
    }
}
