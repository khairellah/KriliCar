package com.kriliCar.security;


import com.kriliCar.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    // 1. On commente l'injection car le service n'existe pas encore : Jusqu'a Ticket US-0.4
    // private final UserDetailsService userDetailsService; // Service pour charger l'utilisateur

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String email = jwtUtils.getEmailFromJwtToken(jwt);
                // ON COMMENTE CETTE PARTIE POUR L'INSTANT : Jusqu'a Ticket US-0.4
                /*

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Crée le jeton d'authentification pour la requête
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Met à jour le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);

                */
            }
        } catch (Exception e) {
            // Loggez l'erreur d'authentification (si le token est là mais invalide, etc.)
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}