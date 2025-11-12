// Lógica del negocio

package com.utabox.auth_service.service;

import com.utabox.auth_service.dto.AuthRequestDTO;
import com.utabox.auth_service.dto.AuthResponseDTO;
import com.utabox.auth_service.dto.RegisterRequestDTO;
import com.utabox.auth_service.model.Usuario;
import com.utabox.auth_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Clase para encriptar
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.utabox.auth_service.util.JwtUtil; // Importarmos el Jwt
import java.sql.Timestamp;

@Service // Le dice a Spring que esta es una clase de "Servicio" (lógica)
public class AuthService {

    @Autowired // Inyección de Dependencias: Spring nos "inyecta" el repositorio
    private UsuarioRepository usuarioRepository;

    @Autowired // Inyectamos el encriptador
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; // Inyectar JwT    

    public Usuario register(RegisterRequestDTO request) {
        // 1. Validar si el email ya existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            System.out.println("⚠️ Email duplicado detectado: " + request.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }

        // 2. Crear el nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        
        // 3. Encriptar contraseña
        usuario.setContrasenaHash(passwordEncoder.encode(request.getPassword()));
        
        usuario.setRol("cliente"); // Rol por defecto
        usuario.setFechaCreacion(new Timestamp(System.currentTimeMillis()));

        // 4. Guardar en la BD
        return usuarioRepository.save(usuario);
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        // 1. Buscar al usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // 2. Comprobar la contraseña
        // Comparamos la "raw" (request.getPassword()) con la "hash" (usuario.getContrasenaHash())
        if (!passwordEncoder.matches(request.getPassword(), usuario.getContrasenaHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 3. Generar el Token JWT (¡¡PENDIENTE!!)
        String token = jwtUtil.generateToken(usuario); 

        // 4. Devolver la respuesta
        return new AuthResponseDTO(token, usuario.getRol(), usuario.getNombre());
    }
}