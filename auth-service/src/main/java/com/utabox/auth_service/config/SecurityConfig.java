package com.utabox.auth_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Le dice a Spring que esta es una clase de configuración
@EnableWebSecurity // Activa la seguridad web
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // 1. Definimos el "Bean" (componente) del encriptador
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Definimos las reglas de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactivamos CSRF (común en APIs REST)
            .authorizeHttpRequests(authz -> authz
                // 3. Hacemos públicos nuestros endpoints de autenticación
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll() 
                // 4. Todas las demás peticiones deben estar autenticadas (lo veremos con JWT)
                .anyRequest().authenticated() 
            )
            // 5. Le decimos que no use "sesiones" (usaremos JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // (Aquí añadiremos el filtro JWT más adelante)

        return http.build();
    }
}