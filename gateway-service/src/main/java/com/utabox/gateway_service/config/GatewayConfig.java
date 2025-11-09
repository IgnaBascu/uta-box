package com.utabox.gateway_service.config;

import com.utabox.gateway_service.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
        
        
    private final AuthenticationFilter authenticationFilter;

    GatewayConfig(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Ruta para el servicio de autenticación
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("lb://AUTH-SERVICE"))
                
                // Ruta para el servicio de catálogo
                .route("catalogo-service", r -> r
                        .path("/api/productos/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://CATALOGO-SERVICE"))
                        
                
                // Ruta para el servicio de reservas
                .route("reservas-service", r -> r
                        .path("/api/reservas/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://RESERVAS-SERVICE"))
                
                .build();
    }
}