package com.kriliCar.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "companies")
// @PrimaryKeyJoinColumn(name = "company_id")
@Getter @Setter @NoArgsConstructor
@SuperBuilder
public class Company extends AppUser {
}