package com.utabox.core_service.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Este método se activa CADA VEZ que falla una validación @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
        // Obtenemos todos los mensajes de error (ej. "El nombre no puede estar vacío")
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", errores); // <-- Aquí van los mensajes claros

        // 2. Devuelve el error 400
        return new ResponseEntity<>(body, org.springframework.http.HttpStatus.BAD_REQUEST);
    }
    
}
