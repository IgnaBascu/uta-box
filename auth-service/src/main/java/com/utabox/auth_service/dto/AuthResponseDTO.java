// Lo que se devuelve al loguearse

package com.utabox.auth_service.dto;

public class AuthResponseDTO {
    private String token;
    private String rol;

    public AuthResponseDTO(String token, String rol) {
        this.token = token;
        this.rol = rol;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
