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


        HttpStatus status = HttpStatus.UNAUTHORIZED; // 401

        // 🚀 Utilisation du Builder pour correspondre à ton nouveau DTO
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Accès refusé. Un jeton d'authentification valide est requis (401).")
                .path(request.getRequestURI())
                // Le timestamp sera généré automatiquement par @Builder.Default dans le DTO
                .build();

        response.setStatus(status.value());
        response.setContentType("application/json");

        // L'ObjectMapper injecté gérera parfaitement le LocalDateTime grâce aux dépendances de Spring Boot 3
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}