package com.kass.auth.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kass.auth.SimpleGrantedAuthorityJsonCreator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;

import static com.kass.auth.TokenJwtConfig.*;
import java.io.IOException;
import java.util.*;

public class JwtValidationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Authorization Header: " + request.getHeader(HEADER_AUTHORIZATION));
        String header = request.getHeader(HEADER_AUTHORIZATION);
        
        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            System.out.println("No JWT token found, allowing request to continue.");
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = header.replace(PREFIX_TOKEN, "");
        
        try {
            Claims claims = Jwts.parser().verifyWith((SecretKey) SECRET_KEY).build().parseSignedClaims(token).getPayload();
            Object authoritiesClaims = claims.get("authorities");
            String username = claims.getSubject();
            System.out.println("Decoded JWT Claims: " + claims);
            System.out.println("Authorities Claims: " + authoritiesClaims);
            Collection<? extends GrantedAuthority> authorities = Arrays.asList(new ObjectMapper().addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class).readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            System.out.println("User authenticated: " + username);
            filterChain.doFilter(request, response);
        } catch (Throwable e) {
            System.out.println("JWT Validation Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Invalid JWT token");
            body.put("error", e.getMessage());
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        }
    }
}
