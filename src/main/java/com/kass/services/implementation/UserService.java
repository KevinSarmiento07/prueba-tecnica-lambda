package com.kass.services.implementation;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kass.dao.RoleDao;
import com.kass.dao.UserDao;
import com.kass.models.Role;
import com.kass.models.User;
import com.kass.services.interfaces.IUserService;

@Service(value = "userService")
public class UserService implements IUserService {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private RoleDao roleDao;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public User save(User user) {
        if(user.getId() != null && user.getId() > 0) {
            return userDao.save(user);
        }
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        user.setEnabled(true);
        Role role = roleDao.findByRole("ROLE_USER");
        if (role != null){
            user.setRoles(Arrays.asList(role));
        }
        return userDao.save(user);
    }
    
    @Override
    public Optional<User> findUserByUsername(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findUserByUsername'");
    }
    
    
    
}
