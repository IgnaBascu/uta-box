package com.utabox.core_service.config; // (Asegúrate que el paquete sea el tuyo)

import com.utabox.core_service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull; // <-- 1. IMPORTA NONNULL
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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain) 
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;        
        final String userRol;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); 
            return;
        }

        jwt = authHeader.substring(7);

        try {
            if (jwtUtil.isTokenValid(jwt)) {
                Claims claims = jwtUtil.getClaims(jwt);
                // 1. EXTRAER EL ID DEL CLAIM (ADEMÁS DEL ROL)                
                Integer usuarioId = claims.get("id", Integer.class); 
                userRol = claims.get("rol", String.class); 

                if (usuarioId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    
                    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRol));
                    
                    // 2. USAR EL ID COMO EL "PRINCIPAL"
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            usuarioId, 
                            null,      
                            authorities
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            // 3. CAMBIAMOS EL 'logger' POR UNA ALERTA SIMPLE
            System.err.println("No se pudo establecer la autenticación del usuario: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}