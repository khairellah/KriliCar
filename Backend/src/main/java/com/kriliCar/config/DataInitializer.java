package com.kriliCar.config;


import com.kriliCar.entities.Admin;
import com.kriliCar.enums.Role;
import com.kriliCar.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@krilicar.com").isEmpty()) {
            Admin admin = Admin.builder()
                    .firstName("System")
                    .lastName("Admin")
                    .email("admin@krilicar.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Utilisateur Admin par défaut créé : admin@krilicar.com / admin123");
        }
    }
}