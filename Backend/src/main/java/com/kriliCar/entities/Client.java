package com.kriliCar.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "clients")
// @PrimaryKeyJoinColumn(name = "client_id")
@Getter @Setter @NoArgsConstructor
@SuperBuilder
public class Client extends AppUser {
}