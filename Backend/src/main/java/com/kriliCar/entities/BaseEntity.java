package com.kriliCar.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass // Cette annotation dit à JPA que cette classe doit être mappée dans les tables des sous-classes.
@SuperBuilder // Utile pour Lombok dans l'héritage
@Data
@NoArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String code;

    // Champs d'audit (optionnel mais très recommandé)
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void generateCode() {
        if (this.code == null) {
            // Génère un code unique avant l'insertion en base
            this.code = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        }
    }
}