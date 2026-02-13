package com.kriliCar.services.impl;

import com.kriliCar.dtos.CompanyProfileRequest;
import com.kriliCar.entities.Company;
import com.kriliCar.repositories.CompanyRepository;
import com.kriliCar.services.interfaces.CompanyService;
import com.kriliCar.services.interfaces.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final FileService fileService; // Ton interface FileService

    @Override
    @Transactional
    public Company updateProfile(String email, CompanyProfileRequest request, MultipartFile imageFile) throws IOException {
        Company company = companyRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Société non trouvée"));

        // Mise à jour des champs
        company.setFirstName(request.getFirstName());
        company.setLastName(request.getLastName());
        company.setPhone(request.getPhone());
        company.setCompanyName(request.getCompanyName());
        company.setLandline(request.getLandline());
        company.setCity(request.getCity());
        company.setDescription(request.getDescription());

        // Gestion de l'image
        if (imageFile != null && !imageFile.isEmpty()) {
            if (company.getImage() != null) {
                fileService.deleteFile(company.getImage());
            }
            String path = fileService.uploadFile(imageFile, "Company");
            company.setImage(path);
        }

        return companyRepository.save(company);
    }
}