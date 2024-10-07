package com.DMH.accountsservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    public String getSubjectFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());  // O el campo donde esté almacenado el userId
    }

    // Extraer todos los claims (reclamaciones) desde el token
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        // Validar solo el token (expiración)
        return !isTokenExpired(token);
    }


    // Método para resolver el token de la cabecera Authorization
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Eliminar "Bearer " para obtener solo el token
        }
        return null; // Si no hay token, retornar null
    }

    public boolean validateTokenWithUser(String token, UserDetails userDetails) {
        // Primero, valida el token
        if (!validateToken(token)) {
            return false; // Si el token no es válido, retornamos false
        }

        // Obtener el email desde el token
        String emailFromToken = getSubjectFromToken(token);

        // Verifica si el email del token coincide con el username de UserDetails
        return emailFromToken.equals(userDetails.getUsername());
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token); // Método para obtener la fecha de expiración
        return expirationDate.before(new Date()); // Verifica si la fecha de expiración es anterior a la fecha actual
    }

    private Date getExpirationDateFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
    }

    }

