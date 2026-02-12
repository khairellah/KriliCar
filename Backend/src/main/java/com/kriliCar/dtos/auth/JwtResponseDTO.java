package com.kriliCar.dtos.auth;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponseDTO {
    private String token;
    //private Long id; pas besoin de fournir l'id dans JwtResponseDTO
    private String email;
    private String role; // Le rôle de l'utilisateur (ADMIN, CLIENT, COMPANY)
    private String code; //  Nouveau champ
}