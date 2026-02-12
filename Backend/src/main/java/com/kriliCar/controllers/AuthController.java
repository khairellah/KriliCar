package com.kriliCar.controllers;


import com.kriliCar.dtos.auth.JwtResponseDTO;
import com.kriliCar.dtos.auth.LoginRequestDTO;
import com.kriliCar.dtos.registration.ClientRegistrationDTO;
import com.kriliCar.dtos.registration.ClientRegistrationResponseDTO;
import com.kriliCar.dtos.registration.CompanyRegistrationDTO;
import com.kriliCar.dtos.registration.CompanyRegistrationResponseDTO;
import com.kriliCar.entities.AppUser;
import com.kriliCar.services.interfaces.AuthService;
import com.kriliCar.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager; // <-- NOUVELLE INJECTION
    private final JwtUtils jwtUtils; // <-- NOUVELLE INJECTION

    // POST /api/v1/auth/register/client
    // Consumes définit le type de requête (multipart/form-data)
    @PostMapping(value = "/register/client", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClientRegistrationResponseDTO> registerClient(
            // @RequestPart est utilisé pour désérialiser la partie JSON de la requête
            @RequestPart("user") ClientRegistrationDTO registrationDTO,
            // MultipartFile gère le fichier d'image
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        ClientRegistrationResponseDTO registeredUser = authService.registerClient(registrationDTO, imageFile);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    // POST /api/v1/auth/register/company
    @PostMapping(value = "/register/company", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyRegistrationResponseDTO> registerCompany(
            @RequestPart("company") CompanyRegistrationDTO registrationDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        CompanyRegistrationResponseDTO registeredCompany = authService.registerCompany(registrationDTO, imageFile);
        return new ResponseEntity<>(registeredCompany, HttpStatus.CREATED);
    }

    // NOTE : La méthode de login sera ajoutée ici plus tard (avec JWT)

    // POST /api/v1/auth/login : Accessible à tous (permitAll)
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        try {
            // 1. Authentification de l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // 2. Définition du contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Génération du token JWT
            String jwt = jwtUtils.generateJwtToken(authentication);

            // 4. Récupération des détails (ton AppUser implémente UserDetails)
            AppUser userDetails = (AppUser) authentication.getPrincipal();

            // Le rôle est extrait de l'autorité (ex: ROLE_CLIENT)
            String userRole = userDetails.getAuthorities().iterator().next().getAuthority();

            return ResponseEntity.ok(JwtResponseDTO.builder()
                    .token(jwt)
                    .email(userDetails.getUsername())
                    .role(userRole)
                    .code(userDetails.getCode()) // On renvoie le code ici
                    .build());

        } catch (BadCredentialsException e) {
            // Retourne un message clair en cas d'erreur d'identifiants
            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.UNAUTHORIZED.value());
            body.put("error", "Unauthorized");
            body.put("message", "Email ou mot de passe incorrect.");
            body.put("path", "/api/v1/auth/login");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
    }
}