package com.kriliCar.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "admins")
@Getter @Setter @NoArgsConstructor
@SuperBuilder
public class Admin extends AppUser {
    // On pourra ajouter des champs spécifiques à l'Admin ici
}