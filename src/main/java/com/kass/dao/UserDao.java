package com.kass.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kass.models.User;

public interface UserDao extends JpaRepository<User, Integer> {
    
    User findByUsername(String username);
    
}
