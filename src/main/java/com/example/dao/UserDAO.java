package com.example.dao;

import com.example.util.TransactionUtil;
import org.hibernate.SessionFactory;
import java.util.List;

import com.example.domain.User;

public class UserDAO {
    //Экземпляр класса с методом стандартной обработки транзакций
    private final TransactionUtil txUtil;

    public UserDAO(SessionFactory sessionFactory) {
        this.txUtil = new TransactionUtil(sessionFactory);
    }

    public void create(User user) {
        txUtil.withTransaction(session -> {
            session.persist(user);
            return null;
        });
    }

    public User getById(Long id) {
        return txUtil.withTransaction(session -> session.get(User.class, id));
    }

    public List<User> getAll() {
        return txUtil.withTransaction(session -> session.createQuery("FROM User", User.class).list());
    }

    public void update(User user) {
        txUtil.withTransaction(session -> {
            session.merge(user);
            return null;
        });
    }

    public void delete(User user) {
        txUtil.withTransaction(session -> {
            session.remove(user);
            return null;
        });
    }

    public TransactionUtil getTxUtil() {
        return txUtil;
    }
}
