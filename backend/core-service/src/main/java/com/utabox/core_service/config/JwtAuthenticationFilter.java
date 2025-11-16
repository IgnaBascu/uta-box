package com.utabox.core_service.config;

import com.utabox.core_service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component // Le dice a Spring que gestione esta clase (para poder inyectarla)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil; // Inyectamos nuestro Util de JWT

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Obtener la cabecera "Authorization"
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        final String userRol;

        // 2. Validar que la cabecera sea válida
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Si no hay token, continúa al siguiente filtro
            return;
        }

        jwt = authHeader.substring(7); // Extrae el token (quitando "Bearer ")

        try {
            // 3. Validar el token usando nuestro JwtUtil
            if (jwtUtil.isTokenValid(jwt)) {
                Claims claims = jwtUtil.getClaims(jwt);
                userEmail = claims.getSubject();
                userRol = claims.get("rol", String.class); // Extraemos el rol que pusimos al crearlo

                // 4. Si el token es válido, autenticamos al usuario en Spring Security
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    
                    // Creamos la lista de "autoridades" (roles)
                    // Importante añadir "ROLE_" como prefijo, es el estándar de Spring Security
                    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRol));
                    
                    // Creamos el objeto de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userEmail, // El "principal" (identificador del usuario)
                            null,      // No usamos credenciales (password) aquí
                            authorities
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Establecemos la autenticación en el contexto de seguridad
                    // A partir de aquí, Spring sabe que el usuario está autenticado
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Si el token es inválido (expirado, malformado, etc.)
            // Limpiamos el contexto y dejamos que la petición continúe sin autenticación.
            SecurityContextHolder.clearContext();
            logger.warn("No se pudo establecer la autenticación del usuario: " + e.getMessage());
        }

        filterChain.doFilter(request, response); // Continúa con el siguiente filtro
    }
}