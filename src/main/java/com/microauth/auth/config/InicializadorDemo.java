package com.microauth.auth.config;

import com.microauth.auth.entity.Rol;
import com.microauth.auth.entity.Usuario;
import com.microauth.auth.repository.RepositorioRol;
import com.microauth.auth.repository.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InicializadorDemo implements CommandLineRunner {
    
    private final RepositorioRol rolRepo;
    private final RepositorioUsuario usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        System.out.println("\n=== INICIALIZADOR DEMO ===");
        
        // 1. Crear roles SOLO si no existen
        if (!rolRepo.existsByNombreRol("ADMINISTRADOR")) {
            Rol adminRol = new Rol();
            adminRol.setNombreRol("ADMINISTRADOR");
            adminRol.setDescripcion("Control total del sistema");
            rolRepo.save(adminRol);
            System.out.println("✅ Rol ADMINISTRADOR creado");
        }
        
        if (!rolRepo.existsByNombreRol("CHEF")) {
            Rol chefRol = new Rol();
            chefRol.setNombreRol("CHEF");
            chefRol.setDescripcion("Responsable de cocina");
            rolRepo.save(chefRol);
            System.out.println("✅ Rol CHEF creado");
        }
        
        if (!rolRepo.existsByNombreRol("CONTADORA")) {
            Rol contadoraRol = new Rol();
            contadoraRol.setNombreRol("CONTADORA");
            contadoraRol.setDescripcion("Gestión financiera");
            rolRepo.save(contadoraRol);
            System.out.println("✅ Rol CONTADORA creado");
        }
        
        // 2. Crear usuario admin DEMO solo si no existe
        if (!usuarioRepo.existsByEmail("admin@demo.com")) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@demo.com");
            admin.setNombre("Admin");
            admin.setApellidos("Demo");
            admin.setDocumento("1000000000");
            admin.setTelefono("3000000000");
            admin.setContraseña(passwordEncoder.encode("admin123"));
            
            // Buscar rol ADMINISTRADOR
            Rol rolAdmin = rolRepo.findByNombreRol("ADMINISTRADOR")
                    .orElseThrow(() -> new RuntimeException("Rol ADMINISTRADOR no encontrado"));
            
            admin.setRol(rolAdmin);
            usuarioRepo.save(admin);
            
            System.out.println("✅ Usuario ADMIN creado: admin@demo.com / admin123");
        }
        
        System.out.println("=== INICIALIZACIÓN COMPLETADA ===");
    }
}