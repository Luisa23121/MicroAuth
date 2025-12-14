package com.microauth.auth.controller;

import com.microauth.auth.entity.Usuario;
import org.springframework.security.core.Authentication;
import java.util.Optional;
import com.microauth.auth.dto.LoginRequest;
import com.microauth.auth.dto.RegistroRequest;
import com.microauth.auth.service.AuthService;
import com.microauth.auth.repository.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login exitoso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales inválidas");
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        try {
            String token = authService.registro(request);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Registro exitoso");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(Authentication authentication) {
        try {
            // authentication.getName() contiene el email del usuario (del token)
            String email = authentication.getName();
            
            // Buscar usuario por email
            Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Usuario no encontrado");
                return ResponseEntity.status(404).body(error);
            }
            
            Usuario usuario = usuarioOpt.get();
            
            
            Map<String, Object> perfil = new HashMap<>();
            perfil.put("id", usuario.getId());
            perfil.put("documento", usuario.getDocumento());
            perfil.put("nombre", usuario.getNombre());
            perfil.put("apellidos", usuario.getApellidos());
            perfil.put("email", usuario.getEmail());
            perfil.put("telefono", usuario.getTelefono());
            perfil.put("fechaCreacion", usuario.getFechaCreacion());
            perfil.put("cuentaBloqueada", usuario.getCuentaBloqueada());
            perfil.put("intentosFallidos", usuario.getIntentosFallidos());
            
            
            if (usuario.getRol() != null && usuario.getRol().getNombreRol() != null) {
                perfil.put("rol", usuario.getRol().getNombreRol());
            } else {
                perfil.put("rol", "NO_ASIGNADO");
            }
            
            return ResponseEntity.ok(perfil);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener el perfil: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    
    @GetMapping("/test-protegido")
    public ResponseEntity<?> testProtegido(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }
        
        return ResponseEntity.ok(Map.of(
            "mensaje", "¡Endpoint protegido funciona!",
            "usuario", authentication.getName(),
            "autenticado", authentication.isAuthenticated()
        ));
    }
}