package com.kriliCar.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kriliCar.dtos.responses.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Cette classe gère les erreurs 403 Forbidden.
 * Elle intervient lorsqu'un utilisateur est authentifié mais n'a pas
 * les privilèges suffisants (rôle) pour accéder à une ressource.
 */

@Component
@RequiredArgsConstructor
public class AuthAccessDeniedHandler implements AccessDeniedHandler {

    // On injecte l'ObjectMapper configuré par Spring (supporte LocalDateTime)
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        // 1. Définition du statut 403
        HttpStatus status = HttpStatus.FORBIDDEN;

        // 2. Construction de la réponse via le Builder de ton DTO
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Accès refusé. Vous n'avez pas les permissions (rôle) nécessaires pour cette ressource.")
                .path(request.getRequestURI())
                .build();

        // 3. Configuration de la réponse HTTP
        response.setStatus(status.value());
        response.setContentType("application/json");
        // 4. Écriture du JSON dans le corps de la réponse
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}