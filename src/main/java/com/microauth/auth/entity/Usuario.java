package com.microauth.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")  // Tabla "usuarios" en MySQL
@Data
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String documento;
    
    private String nombre;
    private String apellidos;
    
    @Column(nullable = false, unique = true)
    private String email;  // Para login
    
    private String telefono;
    
    @Column(nullable = false)
    private String contraseña;  // Se encriptará
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "cuenta_bloqueada")
    private Boolean cuentaBloqueada = false;
    
    @Column(name = "fecha_bloqueo")
    private LocalDateTime fechaBloqueo;
    
    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;
    
    // RELACIÓN: N Usuarios pertenecen a 1 Rol
    @ManyToOne(fetch = FetchType.EAGER)  // Carga el rol inmediatamente
    @JoinColumn(name = "id_rol")  // FOREIGN KEY en la tabla usuarios
    private Rol rol;
}