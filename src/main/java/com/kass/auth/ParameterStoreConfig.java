package com.kass.auth;

import com.kass.services.implementation.ParameterStoreService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//@Component
public class ParameterStoreConfig {
    
    private final ParameterStoreService parameterStoreService;
    
    public ParameterStoreConfig(ParameterStoreService parameterStoreService) {
        this.parameterStoreService = parameterStoreService;
    }
    
    @Value("${spring.datasource.url}")
    private String dbUrl;
    
    @Value("${spring.datasource.username}")
    private String dbUsername;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    //@PostConstruct
    public void init() {
        System.setProperty("PARAM_DB_URL", parameterStoreService.getParameter("/db/url"));
        System.setProperty("PARAM_DB_USERNAME", parameterStoreService.getParameter("/db/username"));
        System.setProperty("PARAM_DB_PASSWORD", parameterStoreService.getParameter("/db/password"));
    }
}
