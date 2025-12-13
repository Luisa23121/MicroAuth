package com.microauth.auth.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DotenvConfig {
    
    @PostConstruct
    public void init() {
        try {
            // Carga .env desde la raíz del proyecto
            Dotenv dotenv = Dotenv.configure()
                    .directory(System.getProperty("user.dir"))
                    .ignoreIfMissing()
                    .load();
            
            // Verifica MySQL
            String dbUrl = dotenv.get("DB_URL");
            System.out.println("✅ .env cargado: " + (dbUrl != null ? "SÍ" : "NO"));
            System.out.println("   MySQL URL: " + (dbUrl != null ? dbUrl : "jdbc:mysql://localhost:3306/microauth_db"));
            
        } catch (Exception e) {
            System.out.println("⚠️  .env no encontrado, usando valores por defecto");
        }
    }
}