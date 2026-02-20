package com.kriliCar.repositories;

import com.kriliCar.entities.Car;
import com.kriliCar.enums.CarAvailability;
import com.kriliCar.enums.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    // Recherche par le code métier (ex: CAR-A1B2C3D4)
    Optional<Car> findByCode(String code);

    // Vérification d'unicité pour le VIN
    boolean existsByVin(String vin);

    // Recherche paginée par compagnie
    Page<Car> findByCompanyId(Long companyId, Pageable pageable);

    // Si tu as besoin de vérifier si un code existe
    boolean existsByCode(String code);

    // Recherche Simple : par Nom de Marque + Nom de Modèle + Ville de la Compagnie + Disponibilité
    @Query("SELECT c FROM Car c WHERE " +
            "(:brand IS NULL OR :brand = '' OR LOWER(c.brand.name) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:model IS NULL OR :model = '' OR LOWER(c.model.name) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
            "(:city IS NULL OR c.company.city = :city) AND " +
            "c.availability = :status")
    Page<Car> searchSimple(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("city") City city,
            @Param("status") CarAvailability status,
            Pageable pageable
    );
}