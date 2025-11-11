package com.utabox.gateway_service.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // Lee la misma clave secreta que definimos en application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Método para crear la "llave secreta" a partir del string
    private SecretKey getSigningKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Método principal para validar y extraer TODOS los datos (claims) del token
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())                
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    // Método para comprobar si el token ha expirado
    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
