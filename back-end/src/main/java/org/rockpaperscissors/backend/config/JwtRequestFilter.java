package org.rockpaperscissors.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rockpaperscissors.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Extraer el token desde el header de la solicitud (puedes personalizar la función si es necesario)
            String token = extractToken(request);

            if (token != null && jwtUtil.isTokenValid(token)) {
                // Si el token es válido, obtenemos la autenticación y la seteamos en el contexto de seguridad
                Authentication authentication = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // En caso de error, logueamos el error (opcional) y el flujo sigue sin interrumpir la ejecución
            e.printStackTrace();
        }

        // Continuamos con el flujo de la cadena de filtros
        filterChain.doFilter(request, response);
    }

    // Extrae el token de la cabecera Authorization
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Extrae el token sin la palabra "Bearer "
        }
        return null;
    }
}
