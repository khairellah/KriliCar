package com.kriliCar.services.interfaces;

import com.kriliCar.dtos.CarDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface CarService {
    CarDTO createCar(CarDTO carDTO, List<MultipartFile> imageFiles, Authentication authentication) throws IOException;

    // Utilisation du CODE partout pour l'identification
    CarDTO updateCar(String code, CarDTO carDTO, Authentication authentication);
    void deleteCar(String code, Authentication authentication);

    CarDTO getCarByCode(String code);
    Page<CarDTO> getAllCars(Long companyId, Pageable pageable);
    boolean isCarOwnedByCompany(Long carId, String email);
}