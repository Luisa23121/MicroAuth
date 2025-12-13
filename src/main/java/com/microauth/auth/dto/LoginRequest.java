package com.microauth.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String contraseña;
}