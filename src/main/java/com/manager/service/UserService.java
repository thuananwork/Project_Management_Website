package com.manager.service;

import com.manager.exception.UserAlreadyExistsException;
import com.manager.model.User;

public interface UserService {
    void addUser(User user) throws UserAlreadyExistsException;
    void changePassword(User user, String newPassword);
    String generatePassword();
    User findByUsername(String userName);
    User findUserById(Long id);

}
