package com.kriliCar.controllers;

import com.kriliCar.dtos.CompanyProfileRequest;
import com.kriliCar.entities.Company;
import com.kriliCar.services.interfaces.CompanyService; // Injection de l'interface
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('COMPANY')") // Seul le rôle COMPANY passe ici
    public ResponseEntity<Company> updateProfile(
            @RequestPart("data") CompanyProfileRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            Principal principal) throws IOException {

        Company updated = companyService.updateProfile(principal.getName(), request, imageFile);
        return ResponseEntity.ok(updated);
    }
}