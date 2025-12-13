package com.microauth.auth.service;

import com.microauth.auth.entity.Usuario;
import com.microauth.auth.repository.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class DetallesUsuarioServicio implements UserDetailsService {

    private final RepositorioUsuario repositorioUsuario;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = repositorioUsuario.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // ⭐⭐ CORRECCIÓN CRÍTICA: Agregar "ROLE_" al nombre del rol ⭐⭐
        String nombreRol = usuario.getRol() != null ? usuario.getRol().getNombreRol() : "USUARIO";
        
        // Spring Security NECESITA el prefijo "ROLE_"
        if (!nombreRol.startsWith("ROLE_")) {
            nombreRol = "ROLE_" + nombreRol;
        }
        
        System.out.println("🔐 CARGANDO USUARIO: " + usuario.getEmail());
        System.out.println("   ROL CON PREFIJO: " + nombreRol);

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getContraseña())
                .roles(nombreRol.replace("ROLE_", "")) // .roles() ya agrega "ROLE_" automáticamente
                .build();
    }
}