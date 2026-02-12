package com.kriliCar.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kriliCar.dtos.responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// cette classe pour : 403 Forbidden
// Ceci gère l'erreur AccessDeniedException (quand un Client tente d'accéder à une route Admin).
@Component
public class AuthAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        // Loggez l'erreur pour le serveur
        // logger.error("Access denied error: {}", accessDeniedException.getMessage());

        // Construit votre DTO de réponse 403
        HttpStatus status = HttpStatus.FORBIDDEN; // 403
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "Accès refusé. Vous n'avez pas les permissions (rôle) nécessaires pour cette ressource.",
                request.getRequestURI()
        );

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getOutputStream().println(objectMapper.writeValueAsString(errorResponse));
    }
}