package com.utabox.auth_service.repository;

import com.utabox.auth_service.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository // Le dice a Spring que esta es una clase de acceso a datos
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email); // Realiza busqueda de usuarios mediante email
}
