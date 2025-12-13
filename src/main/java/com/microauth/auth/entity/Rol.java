package com.microauth.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "roles") // Esta clase se mapea a la tabla "roles" en la BD
@Data // Con esto NO necesitas escribir getters/setters (Lombok lo hace)
public class Rol {
    
    @Id // Esto indica que es la llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Se auto-incrementa
    @Column(name = "id_rol") // Se mapea a la columna "id_rol" en la tabla
    private Long idRol;
    
    @Column(name = "nombre_rol", nullable = false, unique = true)
    private String nombreRol; // Ejemplo: "ADMIN", "USER"
    
    private String descripcion;
    
    // Un ROL puede tener muchos USUARIOS
    @OneToMany(mappedBy = "rol") // "rol" es el nombre del atributo en Usuario.java
    private List<Usuario> usuarios;
}