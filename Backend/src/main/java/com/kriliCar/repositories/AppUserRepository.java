package com.kriliCar.repositories;


import com.kriliCar.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// Créez également l'AppUserRepository, nous en aurons besoin plus tard pour l'authentification générique

@Repository
public interface AppUserRepository  extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

}