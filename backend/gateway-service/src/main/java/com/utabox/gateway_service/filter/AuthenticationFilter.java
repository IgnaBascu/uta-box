package com.utabox.gateway_service.filter;


import com.utabox.gateway_service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // --- 1. Definir Rutas Públicas ---
            // Si la ruta es pública (login o register), la dejamos pasar sin verificar token
            if (path.startsWith("/api/auth/")) {
                return chain.filter(exchange);
            }
            // (Si queremos que ver las salas sea público, también lo añadimos aquí)
            if (path.equals("/api/productos/salas") || path.startsWith("/api/reservas/activo/")) {
                return chain.filter(exchange);
            }

            // --- 2. Verificar el Token ---
            // Si no es pública, debe tener un token.
            HttpHeaders headers = request.getHeaders();
            if (!headers.containsKey(HttpHeaders.AUTHORIZATION) || !headers.get(HttpHeaders.AUTHORIZATION).get(0).startsWith("Bearer ")) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED); // 401 si no hay token
            }

            String token = headers.get(HttpHeaders.AUTHORIZATION).get(0).substring(7);

            try {
                // --- 3. Validar el Token ---
                if (jwtUtil.isTokenExpired(token)) {
                    return this.onError(exchange, HttpStatus.UNAUTHORIZED); // 401 si expiró
                }

                Claims claims = jwtUtil.getClaims(token);
                String rol = claims.get("rol", String.class);
                String userId = claims.get("id", Integer.class).toString();

                // --- 4. Autorización por Roles ---
                // Aquí definimos las reglas de tu PDF
                
                // Si la ruta es de ADMIN (POST, PUT, DELETE en productos)
                if (path.startsWith("/api/productos") && !request.getMethod().toString().equals("GET")) {
                    if (!rol.equals("admin")) {
                        return this.onError(exchange, HttpStatus.FORBIDDEN); // 403 si no es admin
                    }
                }

                // Si la ruta es de CLIENTE (reservar)
                if (path.startsWith("/api/reservas/reservar")) {
                    if (!rol.equals("cliente")) {
                        return this.onError(exchange, HttpStatus.FORBIDDEN); // 403 si no es cliente
                    }
                }
                
                // --- 5. Pasar Información al Microservicio ---
                // Si el token es válido, añadimos el ID y ROL a las cabeceras
                // para que 'reservas-service' y 'catalogo-service' los lean
                ServerHttpRequest newRequest = exchange.getRequest().mutate()
                        .header("X-Usuario-Id", userId)
                        .header("X-Usuario-Rol", rol)
                        .build();

                return chain.filter(exchange.mutate().request(newRequest).build());

            } catch (Exception e) {
                // Si el token está malformado o es inválido
                return this.onError(exchange, HttpStatus.UNAUTHORIZED);
            }
        };
    }

    // Método helper para devolver un error
    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    // Clase de configuración vacía (necesaria por el Factory)
    public static class Config {
    }
}
