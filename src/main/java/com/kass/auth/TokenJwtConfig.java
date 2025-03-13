package com.kass.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class TokenJwtConfig {
    
    private static final String SECRET_BASE64 = "fGht5ErH4yT9p2LxMvJwVqZcNsB7dKdX";
    
    public static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_BASE64.getBytes());
    
    public static final String PREFIX_TOKEN = "Bearer ";
    
    public static final String HEADER_AUTHORIZATION = "Authorization";
}
