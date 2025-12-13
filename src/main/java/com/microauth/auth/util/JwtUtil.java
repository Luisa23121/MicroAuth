package com.microauth.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private int expirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ⭐⭐ MÉTODO 100% SEGURO - TOKENS SIEMPRE DIFERENTES ⭐⭐
    public String generarToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // 1. Email como claim
        claims.put("email", userDetails.getUsername());
        
        // 2. Roles como claim  
        claims.put("roles", userDetails.getAuthorities());
        
        // 3. ID ÚNICO (esto garantiza tokens diferentes)
        claims.put("jti", UUID.randomUUID().toString());
        
        // 4. Timestamp exacto
        claims.put("iat", System.currentTimeMillis());
        
        System.out.println("🎯 TOKEN ÚNICO generado para: " + userDetails.getUsername());
        System.out.println("   ID Único: " + claims.get("jti"));
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        final String username = extraerUsername(token);
        return (username.equals(userDetails.getUsername()) && !tokenExpirado(token));
    }

    private <T> T extraerClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerTodosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean tokenExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    private Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }
}