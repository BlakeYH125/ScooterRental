package org.scooterrental.repository.daointerface;

import org.scooterrental.model.entity.User;

import java.util.List;

public interface UserDao {
    void create(User user);
    void update(User user);
    User findUser(Long userId);
    List<User> findUsers();
}
