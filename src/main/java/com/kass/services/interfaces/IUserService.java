package com.kass.services.interfaces;

import java.util.Optional;

import com.kass.models.User;

public interface IUserService {
    
    public User save(User user);
    
    public Optional<User> findUserByUsername(String username);
}

