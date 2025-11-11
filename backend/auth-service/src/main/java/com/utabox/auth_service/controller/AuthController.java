package com.utabox.auth_service.controller;

import com.utabox.auth_service.dto.AuthRequestDTO;
import com.utabox.auth_service.dto.AuthResponseDTO;
import com.utabox.auth_service.dto.RegisterRequestDTO;
import com.utabox.auth_service.model.Usuario;
import com.utabox.auth_service.service.AuthService;
import jakarta.validation.Valid; // Importante para que @Valid funcione
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Le dice a Spring que esta clase es un Controlador API REST
@RequestMapping("/api/auth") // Todas las URLs de esta clase empezarán con /api/auth
public class AuthController {

    @Autowired // Inyectamos el cerebro (el servicio)
    private AuthService authService;

    // Define el endpoint POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        // (@Valid) valida el DTO (ej. @Email, @NotEmpty)
        // (@RequestBody) convierte el JSON en un objeto AuthRequestDTO
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response); // Devuelve 200 OK
    }

    // Define el endpoint POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@Valid @RequestBody RegisterRequestDTO request) {
        Usuario nuevoUsuario = authService.register(request);
        return ResponseEntity.status(201).body(nuevoUsuario); // Devuelve 201 Created
    }
}