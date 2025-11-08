// Lo que se pide para registrarse

package com.utabox.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

    @NotEmpty(message = "El nombre no puede estar vacío")
    private String nombre;

    @Email(message = "El email debe ser válido")
    @NotEmpty(message = "El email no puede estar vacío")
    private String email;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

     // Getters

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    

    

   
    
    
}
