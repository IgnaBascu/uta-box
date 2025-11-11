package com.utabox.auth_service.model;

import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // Le dice a Spring que esta clase es un modelo de BD
@Table(name = "usuarios") // El nombre de la tabla en PostgreSQL
public class Usuario {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Le dice a Postgre que es 'Serial'
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "contrasena_hash", nullable = false)
    private String contrasenaHash;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Timestamp fechaCreacion;

    // --- Getters y Setters ---

    public Usuario() {
        // Constructor vacío para JPA
    }

    // Getters (Obtención datos)

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public String getRol() {
        return rol;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    // Setters (Ingreso datos)

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

}
