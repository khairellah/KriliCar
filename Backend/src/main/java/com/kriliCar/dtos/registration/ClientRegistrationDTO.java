package com.kriliCar.dtos.registration;


import lombok.Builder;
import lombok.Data;

// Nous utilisons @Data et @Builder pour la simplicité
@Data
@Builder
public class ClientRegistrationDTO {

    // Champs de base hérités de AppUser
    private String firstName;
    private String lastName;
    private String email;
    private String password; // Sera haché dans le service
    private String phone;
    private String image;
}