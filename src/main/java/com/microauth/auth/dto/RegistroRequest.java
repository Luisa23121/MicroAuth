package com.microauth.auth.dto;

import lombok.Data;

@Data
public class RegistroRequest {
    private String documento;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String contraseña;
    private String rol;
}