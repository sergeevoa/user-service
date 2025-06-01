package com.example.service;

import com.example.dao.UserDAO;
import com.example.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public class UserService {
    private final UserDAO dao;

    public UserService(UserDAO dao) {
        this.dao = dao;
    }

    public void create(String name, String email, int age) {
        dao.create(new User(name, email, age, LocalDateTime.now()));
    }

    public User get(Long id) {
        return dao.getById(id);
    }

    public List<User> getAll() {
        return dao.getAll();
    }

    public User update(Long id, String newName, String newEmail, Integer newAge) {
        User user = get(id);
        if (user != null) {
            if (newName != null && !newName.isBlank()) {
                user.setName(newName);
            }
            if (newEmail != null && !newEmail.isBlank()) {
                user.setEmail(newEmail);
            }
            if (newAge != null) {
                user.setAge(newAge);
            }
            dao.update(user);
        }
        return user;
    }

    public User delete(Long id) {
        User user = get(id);
        if (user != null)
            dao.delete(user);
        return user;
    }
}
