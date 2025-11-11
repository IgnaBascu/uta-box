package com.utabox.reservas_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// --- ¡IMPORTACIONES AÑADIDAS! ---
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
// ---------------------------------

@Configuration
@EnableWebSecurity // <-- Ahora esto se encontrará
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // <-- Y esto
        http
            .csrf(csrf -> csrf.disable()) // Desactivamos CSRF
            .authorizeHttpRequests(authz -> authz
                // Permitimos TODAS las peticiones a este microservicio
                .requestMatchers("/api/reservas/**").permitAll() 
                .anyRequest().permitAll() // Permite todo lo demás
            );

        return http.build();
    }
}