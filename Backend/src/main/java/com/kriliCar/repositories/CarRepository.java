package com.kriliCar.repositories;

import com.kriliCar.entities.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}