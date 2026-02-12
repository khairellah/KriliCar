package com.kriliCar.utils;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    // Clé de signature JWT
    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Génération du token
    public String generateJwtToken(Authentication authentication) {

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        // Récupérer les rôles sous forme de chaîne (ROLE_ADMIN, ROLE_CLIENT, etc.)
        String roles = userPrincipal.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // L'email de l'utilisateur
                .claim("roles", roles) // Ajout des rôles dans le corps du token
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    // Validation du token (méthodes à utiliser par le filtre de sécurité)
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            // Loggez l'erreur ici (token expiré, invalide, etc.)
            // System.err.println("JWT invalide: " + e.getMessage());
        }
        return false;
    }

    // Récupération de l'email à partir du token
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}