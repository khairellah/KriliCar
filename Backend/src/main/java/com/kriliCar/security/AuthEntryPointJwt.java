package com.kriliCar.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kriliCar.dtos.responses.ErrorResponse; // Votre DTO d'erreur
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// cette classe pour : 401 Unauthorized

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    // 1. Déclaration de l'ObjectMapper (non initialisé ici)
    private final ObjectMapper objectMapper;

    // 2. Injection via le constructeur (Spring fournit l'instance configurée)
    public AuthEntryPointJwt(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Loggez l'erreur pour le serveur
        // logger.error("Unauthorized error: {}", authException.getMessage());

        // Construit votre DTO de réponse 401
        HttpStatus status = HttpStatus.UNAUTHORIZED; // 401

        // Comme l'erreur de sérialisation s'est produite ici, nous allons construire
        // une réponse manuelle simple pour s'assurer que l'erreur est capturée.
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "Accès refusé. Un jeton d'authentification valide est requis (401).",
                request.getRequestURI()
        );

        response.setStatus(status.value());
        response.setContentType("application/json");

        // 3. Utilisation de l'ObjectMapper injecté
        // C'est cette ligne qui causait l'erreur "Java 8 date/time type not supported"
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}