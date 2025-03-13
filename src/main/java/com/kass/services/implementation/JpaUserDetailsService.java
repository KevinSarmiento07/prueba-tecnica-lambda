package com.kass.services.implementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kass.dao.UserDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JpaUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserDao userDao;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        com.kass.models.User user = userDao.findByUsername(username);
        
        if(user == null){
            throw  new UsernameNotFoundException(String.format("El username: %s no se encuentra en la base de datos.", username));
        }
        
        
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
        return new User(user.getUsername(), user.getPassword(), user.isEnabled(),true, true, true, authorities);
    }
}