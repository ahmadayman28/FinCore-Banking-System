package com.fincore.fincorebank.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.addAllowedOriginPattern("*"); 
        
        configuration.addAllowedHeader("*");
        
        configuration.addAllowedMethod("*");
        
        configuration.setAllowCredentials(true);
        
        configuration.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", configuration);
        
        return new CorsFilter(source);
    }
}