package com.microauth.auth.controller;

import com.microauth.auth.entity.Usuario;
import com.microauth.auth.repository.RepositorioRol;
import com.microauth.auth.repository.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UsuarioController {
    
    private final RepositorioUsuario usuarioRepo;
    private final RepositorioRol rolRepo;
    
    // 1. LISTAR TODOS (solo ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioRepo.findAll());
    }
    
    // 2. OBTENER POR ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        return usuarioRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 3. ACTUALIZAR
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, 
                                        @RequestBody Map<String, String> datos) {
        return usuarioRepo.findById(id)
                .map(usuario -> {
                    if (datos.containsKey("nombre")) usuario.setNombre(datos.get("nombre"));
                    if (datos.containsKey("telefono")) usuario.setTelefono(datos.get("telefono"));
                    if (datos.containsKey("documento")) usuario.setDocumento(datos.get("documento"));
                    
                    usuarioRepo.save(usuario);
                    
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("mensaje", "Usuario actualizado");
                    respuesta.put("id", id);
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 4. ELIMINAR
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!usuarioRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        usuarioRepo.deleteById(id);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Usuario eliminado correctamente");
        respuesta.put("id", id);
        return ResponseEntity.ok(respuesta);
    }
    
    // 5. BUSCAR POR EMAIL
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> buscar(@RequestParam String email) {
        List<Usuario> usuarios = usuarioRepo.findByEmailContainingIgnoreCase(email);
        
        if (usuarios.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "No se encontraron usuarios con email: " + email);
            return ResponseEntity.ok(error);
        }
        
        return ResponseEntity.ok(usuarios);
    }
    
    // 6. CAMBIAR ROL (solo ADMIN)
    @PutMapping("/{id}/cambiar-rol")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> cambiarRol(@PathVariable Long id, 
                                        @RequestParam String nuevoRol) {
        return usuarioRepo.findById(id)
                .map(usuario -> {
                    return rolRepo.findByNombreRol(nuevoRol.toUpperCase())
                            .map(rol -> {
                                usuario.setRol(rol);
                                usuarioRepo.save(usuario);
                                
                                Map<String, Object> respuesta = new HashMap<>();
                                respuesta.put("mensaje", "Rol actualizado");
                                respuesta.put("usuario", usuario.getEmail());
                                respuesta.put("nuevoRol", nuevoRol);
                                return ResponseEntity.ok(respuesta);
                            })
                            .orElse(ResponseEntity.badRequest()
                                    .body(Map.of("error", "Rol no válido: " + nuevoRol)));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}