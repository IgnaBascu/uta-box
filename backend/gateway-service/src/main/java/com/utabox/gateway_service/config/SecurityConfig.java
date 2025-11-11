package com.utabox.gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity // Seguridad Reactiva (para Gateway)
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable()) // Desactivamos CSRF
            .authorizeExchange(exchange -> exchange
                .anyExchange().permitAll() // Permitimos todo a nivel de Spring Security
            );
            // Dejamos que nuestro filtro personalizado ('AuthenticationFilter') se encargue de todo
            
        
        return http.build();
    }
}