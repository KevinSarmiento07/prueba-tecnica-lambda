package com.kass.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kass.models.Role;

public interface RoleDao extends JpaRepository<Role, Integer> {
    
    Role findByRole(String role);
    
}

