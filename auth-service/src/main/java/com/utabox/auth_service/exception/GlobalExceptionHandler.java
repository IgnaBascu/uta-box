package com.utabox.auth_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.HashMap;

@ControllerAdvice // Le dice a Spring que esta clase vigila todos los Controladores
public class GlobalExceptionHandler {

    // Este método se activará CADA VEZ que alguien lance un
    // 'ResponseStatusException'
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {

        // 1. Prepara un cuerpo de respuesta JSON limpio
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("status", ex.getStatusCode().value());
        body.put("error", ex.getReason()); // "El email ya está registrado"
        body.put("message", ex.getMessage());

        // 2. Devuelve el error con el código de estado CORRECTO (ej. 409)
        return new ResponseEntity<>(body, ex.getStatusCode());
    }
}