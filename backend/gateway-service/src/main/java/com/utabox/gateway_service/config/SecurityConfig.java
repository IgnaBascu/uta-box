package com.utabox.gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

// --- IMPORTACIONES NECESARIAS PARA CORS ---
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
// ------------------------------------------

@Configuration
@EnableWebFluxSecurity // Seguridad Reactiva (para Gateway)
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            // --- 1. AÑADIMOS LA CONFIGURACIÓN DE CORS ---
            .cors(cors -> cors.configurationSource(createCorsConfigSource()))
            
            // --- 2. TUS REGLAS (ya estaban) ---
            .csrf(csrf -> csrf.disable()) // Desactivamos CSRF
            .authorizeExchange(exchange -> exchange
                .anyExchange().permitAll() // Permitimos todo a nivel de Spring Security
            );
            // Dejamos que nuestro filtro personalizado ('AuthenticationFilter') se encargue de todo
            
        
        return http.build();
    }

    // --- 3. MÉTODO NUEVO QUE CREA LA CONFIGURACIÓN DE CORS ---
    @Bean
    public CorsConfigurationSource createCorsConfigSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Asegúrate de que este puerto (ej. 5173) es donde corre tu app de Vue
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173", // <-- Revisa este puerto
            "http://127.0.0.1:5173"  // <-- Revisa este puerto
        ));
        
        // Permitimos todos los métodos y cabeceras
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // ¡MUY IMPORTANTE! Permite que el frontend envíe credenciales (como el token)
        config.setAllowCredentials(true); 

        // Aplica esta configuración a todas las rutas del Gateway
        source.registerCorsConfiguration("/**", config); 
        return source;
    }
}