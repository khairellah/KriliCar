package com.kriliCar.services.impl;

import com.kriliCar.dtos.CarDTO;
import com.kriliCar.entities.*;
import com.kriliCar.enums.CarAvailability;
import com.kriliCar.exceptions.DuplicateResourceException;
import com.kriliCar.exceptions.ResourceNotFoundException;
import com.kriliCar.exceptions.UnauthorizedActionException;
import com.kriliCar.mappers.CarMapper;
import com.kriliCar.repositories.*;
import com.kriliCar.services.interfaces.CarService;
import com.kriliCar.services.interfaces.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final CompanyRepository companyRepository;
    private final CarMapper carMapper;
    private final FileService fileService;

    private static final String RESOURCE_NAME = "Car";

    @Override
    @Transactional
    public CarDTO createCar(CarDTO carDTO, List<MultipartFile> imageFiles, Authentication authentication) throws IOException {

        // 1. Validation VIN unique (Utilisation de ton exception personnalisée)
        if (carRepository.existsByVin(carDTO.getVin())) {
            throw new DuplicateResourceException(RESOURCE_NAME, "VIN", carDTO.getVin());
        }

        // 2. Récupération de la Company via le token (Sécurité par email)
        Company company = companyRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "email", authentication.getName()));

        // 3. Récupération du Modèle par CODE (Lève 404 si le code est faux)
        Model model = modelRepository.findByCode(carDTO.getModelCode())
                .orElseThrow(() -> new ResourceNotFoundException("Model", "code", carDTO.getModelCode()));

        // 4. Validation de cohérence Marque/Modèle
        if (model.getBrand() == null || !model.getBrand().getCode().equals(carDTO.getBrandCode())) {
            throw new IllegalArgumentException("Le modèle spécifié n'appartient pas à la marque fournie.");
        }

        // 5. Mapping et préparation de l'entité
        Car car = carMapper.toEntity(carDTO);
        car.setCode("CAR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        car.setBrand(model.getBrand());
        car.setModel(model);
        car.setCompany(company);

        // 6. Sauvegarde initiale
        Car savedCar = carRepository.save(car);

        // 7. Traitement des images (KC-20)
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<CarImage> images = new ArrayList<>();
            for (int i = 0; i < imageFiles.size(); i++) {
                String path = fileService.uploadFile(imageFiles.get(i), "cars");
                images.add(CarImage.builder()
                        .car(savedCar)
                        .path(path)
                        .sortOrder(i)
                        .build());
            }
            savedCar.setImages(images);
            savedCar = carRepository.save(savedCar);
        }

        return carMapper.toDTO(savedCar);
    }

    @Override
    @Transactional
    public CarDTO updateCar(String code, CarDTO carDTO, Authentication authentication) {
        // 1. Recherche par CODE (Lève 404 si le code n'existe pas)
        Car car = carRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "code", code));

        // 2. Vérification de sécurité (Propriété de la voiture)
        // On s'assure que la compagnie qui demande la modification est bien la propriétaire
        if (!isCarOwnedByCompany(car.getId(), authentication.getName())) {
            throw new UnauthorizedActionException("Vous n'avez pas le droit de modifier cette voiture.");
        }

        // 3. Mise à jour partielle sécurisée
        if (carDTO.getMileage() != null) car.setMileage(carDTO.getMileage());
        if (carDTO.getPrice() != null) car.setPrice(carDTO.getPrice());
        if (carDTO.getAvailability() != null) car.setAvailability(carDTO.getAvailability());
        if (carDTO.getDescription() != null) car.setDescription(carDTO.getDescription());
        if (carDTO.getNbrSeats() != null) car.setNbrSeats(carDTO.getNbrSeats());
        if (carDTO.getColor() != null) car.setColor(carDTO.getColor());

        // 4. Gestion du VIN avec vérification d'unicité
        if (carDTO.getVin() != null && !car.getVin().equals(carDTO.getVin())) {
            if (carRepository.existsByVin(carDTO.getVin())) {
                throw new DuplicateResourceException("Car", "VIN", carDTO.getVin());
            }
            car.setVin(carDTO.getVin());
        }

        return carMapper.toDTO(carRepository.save(car));
    }

    @Override
    @Transactional
    public void deleteCar(String code, Authentication authentication) {
        // 1. Récupérer la voiture (avec ses images chargées)
        Car car = carRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "code", code));

        // 2. Vérification de sécurité
        if (!isCarOwnedByCompany(car.getId(), authentication.getName())) {
            throw new UnauthorizedActionException("Action non autorisée sur ce véhicule.");
        }

        // 3. Suppression physique des fichiers sur le disque (KC-20)
        if (car.getImages() != null && !car.getImages().isEmpty()) {
            log.info("Suppression des {} fichiers physiques pour la voiture {}", car.getImages().size(), code);
            for (CarImage image : car.getImages()) {
                // Ton FileService gère déjà le try-catch en interne
                fileService.deleteFile(image.getPath());
            }
        }

        // 4. Suppression de l'entité en base de données
        // Grâce à orphanRemoval = true et CascadeType.ALL sur la liste d'images dans l'entité Car,
        // les entrées dans la table car_images seront automatiquement supprimées.
        carRepository.delete(car);
        log.info("Voiture {} supprimée de la base de données", code);
    }

    @Override
    @Transactional(readOnly = true)
    public CarDTO getCarByCode(String code) {
        return carRepository.findByCode(code)
                .map(carMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "code", code));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarDTO> getAllCars(Long companyId, Pageable pageable) {
        if (companyId != null) {
            return carRepository.findByCompanyId(companyId, pageable).map(carMapper::toDTO);
        }
        return carRepository.findAll(pageable).map(carMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCarOwnedByCompany(Long carId, String email) {
        // Si l'utilisateur n'est pas trouvé
        Company company = companyRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "email", email));

        // Si la voiture n'existe pas, on lève 404 (prioritaire sur le 403)
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, "id", carId));

        return car.getCompany().getId().equals(company.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarDTO> searchSimple(String brand, String model, String city, Pageable pageable) throws BadRequestException {
        try {
            com.kriliCar.enums.City cityEnum = null;
            if (city != null && !city.trim().isEmpty()) {
                cityEnum = com.kriliCar.enums.City.valueOf(city.toUpperCase());
            }

            // On appelle la nouvelle méthode searchSimple
            return carRepository.searchSimple(
                    brand,
                    model,
                    cityEnum,
                    CarAvailability.AVAILABLE,
                    pageable
            ).map(carMapper::toDTO);

        } catch (IllegalArgumentException e) {
            log.warn("Tentative de recherche avec une ville invalide : {}", city);
            throw new BadRequestException("La ville spécifiée '" + city + "' n'est pas valide.");
        } catch (Exception e) {
            log.error("ERREUR CRITIQUE DANS SEARCH_SIMPLE : ", e);
            throw e;
        }
    }
}