package org.rockpaperscissors.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Generar una clave secreta segura de 512 bits
    private static final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // 5 horas

    // Generar el token con un nombre de usuario
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // Extraer las claims del token
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extraer el nombre de usuario del token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Verificar si el token ha expirado
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Validar el token comparando el username y si no ha expirado
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    // Metodo para obtener la clave de firma segura
    private Key getSigningKey() {
        return secretKey;
    }

    // Obtener la autenticación desde el token
    public Authentication getAuthentication(String token) {
        String username = extractUsername(token);
        return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
    }

    // **Aquí va el metodo isTokenValid que estaba faltando**
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token); // Solo verifica que el token no esté expirado
    }

}