package org.scooterrental.repository.daoimpl;

import org.hibernate.SessionFactory;
import org.scooterrental.model.entity.User;
import org.scooterrental.repository.daointerface.UserDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    private final SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(User user) {
        sessionFactory.getCurrentSession().persist(user);
    }

    @Override
    public void update(User user) {
        sessionFactory.getCurrentSession().merge(user);
    }

    @Override
    public User findUser(Long userId) {
        return sessionFactory.getCurrentSession().get(User.class, userId);
    }

    @Override
    public List<User> findUsers() {
        return sessionFactory.getCurrentSession().createQuery("FROM User", User.class).list();
    }
}
