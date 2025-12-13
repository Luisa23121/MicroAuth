package com.microauth.auth.config;

import com.microauth.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Component
public class JwtFiltroAutenticacion extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // Extraer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtUtil.extraerUsername(jwt);
            
            // ⭐⭐ DEBUG CRÍTICO ⭐⭐
            System.out.println("🔐 FILTRO JWT - Usuario: " + username);
        }

        // Si tenemos username y no hay autenticación actual
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // ⭐⭐ CARGAR UserDetails CON ROLES ⭐⭐
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            // ⭐⭐ DEBUG: Verificar roles cargados ⭐⭐
            System.out.println("   ✅ ROLES cargados: " + userDetails.getAuthorities());
            
            if (jwtUtil.validarToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()); // ← AQUÍ VAN LOS ROLES
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}