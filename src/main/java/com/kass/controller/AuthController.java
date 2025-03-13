package com.kass.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.kass.auth.TokenJwtConfig.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kass.models.User;
import com.kass.services.interfaces.IUserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private IUserService userService;
    
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody @Valid User user, BindingResult result) {
        if(result.hasErrors()) {
            return validation(result);
        }
        try {
            userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch(Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, Object> map = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            map.put(err.getField(), err.getDefaultMessage());
        });
        
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> requestBody) {
        try {
            // Obtener el refresh token del request
            String refreshToken = requestBody.get("refreshToken");
            
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("message", "Refresh token is required"));
            }
            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) SECRET_KEY)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();
            
            String username = claims.getSubject();
            if (username == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid refresh token"));
            }
            
            String newAccessToken = Jwts.builder()
                    .claim("authorities", new ObjectMapper().writeValueAsString(authorities))
                    .subject(username)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 900_000))
                    .signWith(SECRET_KEY)
                    .compact();
            
            String newRefreshToken = Jwts.builder()
                    .subject(username)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // Expira en 7 días
                    .signWith(SECRET_KEY)
                    .compact();
            
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("refreshToken", newRefreshToken);
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid refresh token", "error", e.getMessage()));
        }
    }
}

