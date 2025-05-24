package com.moneysaving.service;

import com.moneysaving.model.User;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
    void updateLastLogin(Long userId);
} 