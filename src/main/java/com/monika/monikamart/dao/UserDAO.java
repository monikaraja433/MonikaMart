package com.monika.monikamart.dao;

import com.monika.monikamart.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User create(User user);
    Optional<User> findById(int id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    boolean update(User user);
    boolean delete(int id);
    boolean existsByEmail(String email);
    int countUsers();
}
