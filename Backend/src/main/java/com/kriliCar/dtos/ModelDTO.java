package com.kriliCar.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDTO {
    private String code;      // Code du modèle
    private String name;      // Nom du modèle
    private String brandCode; // Code de la marque associée
    private String brandName; // Pour l'affichage Front-end
}