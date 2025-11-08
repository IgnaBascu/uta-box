// Lo que se pide para el login

package com.utabox.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class AuthRequestDTO {
    @Email
    @NotEmpty
    private String email;
    @NotEmpty

    // Getters
    private String password;
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    

    
    
}
