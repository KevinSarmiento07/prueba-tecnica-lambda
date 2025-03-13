package com.kass.auth.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kass.models.User;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static com.kass.auth.TokenJwtConfig.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    
    private AuthenticationManager authenticationManager;
    
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/auth/login");
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        
        User user = null;
        String username = null;
        String password = null;
        
        try {
            request.setCharacterEncoding("UTF-8");
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword();
            logger.info("username: " + username + " , password: " + password);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authToken);
        
    }
    
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername();
        
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Map<String, String> tokens = generateTokenPair(username, authorities);
        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + tokens.get("accessToken"));
        Map<String, Object> body = new HashMap<>();
        body.put("accessToken", tokens.get("accessToken"));
        body.put("refreshToken", tokens.get("refreshToken"));
        body.put("username", username);
        body.put("message", String.format("Hola, %s has iniciado sesión con éxito.", username));
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("applicaction/json");
    }
    
    private Map<String, String> generateTokenPair(String username, Collection<? extends GrantedAuthority> authorities) throws IOException {
        String accessToken = Jwts.builder()
                .claim("authorities", new ObjectMapper().writeValueAsString(authorities))
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 48 * 60 * 60 * 1000))
                .signWith(SECRET_KEY)
                .compact();
        
        String refreshToken = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000))
                .signWith(SECRET_KEY)
                .compact();
        
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }
    
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Ha ocurrido un error en la autenticación, user o pass incorrecto");
        body.put("error", failed.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType("application/json");
    }
}