package com.microauth.auth.service;

import com.microauth.auth.dto.LoginRequest;
import com.microauth.auth.dto.RegistroRequest;
import com.microauth.auth.entity.Rol;
import com.microauth.auth.entity.Usuario;
import com.microauth.auth.repository.RepositorioRol;
import com.microauth.auth.repository.RepositorioUsuario;
import com.microauth.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class AuthService {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private RepositorioUsuario repositorioUsuario;
    @Autowired private RepositorioRol repositorioRol;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private DetallesUsuarioServicio detallesUsuarioServicio;

    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getContraseña())
        );
        
        UserDetails userDetails = detallesUsuarioServicio.loadUserByUsername(request.getEmail());
        return jwtUtil.generarToken(userDetails);
    }

    public String registro(RegistroRequest request) {
        // ============ SOLO 3 ROLES ============
        String rolSolicitado = request.getRol();
        
        // 1. BLOQUEAR ADMIN
        if (rolSolicitado != null && rolSolicitado.equalsIgnoreCase("ADMINISTRADOR")) {
            throw new RuntimeException("❌ ACCESO DENEGADO: No puedes registrarte como ADMINISTRADOR");
        }
        
        // 2. Si no envía rol, asignar USUARIO
        if (rolSolicitado == null || rolSolicitado.trim().isEmpty()) {
            rolSolicitado = "USUARIO";
        }
        
        // 3. Validar SOLO 3 roles permitidos
        rolSolicitado = rolSolicitado.toUpperCase();
        List<String> rolesPermitidos = Arrays.asList("CHEF", "CONTADORA", "USUARIO");
        
        if (!rolesPermitidos.contains(rolSolicitado)) {
            rolSolicitado = "USUARIO"; // Si no es válido, USUARIO por defecto
        }
        // ============ FIN VALIDACIÓN ============
        
        // 4. Verificar si el usuario ya existe
        if (repositorioUsuario.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (repositorioUsuario.existsByDocumento(request.getDocumento())) {
            throw new RuntimeException("El documento ya está registrado");
        }
        
        
        final String rolFinal = rolSolicitado; 
        
        // 6. Buscar o crear el rol
        Rol rolUsuario = repositorioRol.findByNombreRol(rolFinal)
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombreRol(rolFinal);
                    nuevoRol.setDescripcion("Rol de " + rolFinal.toLowerCase());
                    return repositorioRol.save(nuevoRol);
                });
        
        // 7. Crear usuario
        Usuario usuario = new Usuario();
        usuario.setDocumento(request.getDocumento());
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setContraseña(passwordEncoder.encode(request.getContraseña()));
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setRol(rolUsuario);
        
        // 8. Guardar
        repositorioUsuario.save(usuario);
        
     // 9. Generar token
        UserDetails userDetails = detallesUsuarioServicio.loadUserByUsername(request.getEmail());

        
        System.out.println("✅ REGISTRO EXITOSO - Usuario: " + request.getEmail());

        return jwtUtil.generarToken(userDetails);
}
}