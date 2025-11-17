// Asegúrate de que el package sea el correcto:
package com.utabox.core_service.config; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// --- IMPORTACIONES PARA CORS ---
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {    

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Bean opcional
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Cors y csrf
            .cors(cors -> cors.configurationSource(createCorsConfigSource()))            
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // 2. Hacemos públicas las rutas GET de catalogo y agenda
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reservas/activo/**").permitAll()
                // 3. Rutas admin
                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("admin")
                .requestMatchers(HttpMethod.POST, "/api/productos/salas").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/productos/salas/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/salas/**").hasRole("admin")
                // 4. El resto de rutas SÍ necesitan autenticación (Admin o Cliente)
                .anyRequest().authenticated() 
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 4. AÑADIMOS EL FILTRO JWT            
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 5. AÑADIMOS EL MÉTODO DE CORS
    @Bean
    public CorsConfigurationSource createCorsConfigSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173", 
            "http://127.0.0.1:5173"
        ));
        
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true); 

        source.registerCorsConfiguration("/**", config); 
        return source;
    }
}