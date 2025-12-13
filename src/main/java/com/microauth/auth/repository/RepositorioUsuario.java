package com.microauth.auth.repository;

import com.microauth.auth.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByEmailContainingIgnoreCase(String email);
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    boolean existsByDocumento(String documento);
}