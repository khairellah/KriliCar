package com.kriliCar.services.interfaces;


import com.kriliCar.dtos.CompanyProfileRequest;
import com.kriliCar.entities.Company;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface CompanyService {
    Company updateProfile(String email, CompanyProfileRequest request, MultipartFile imageFile) throws IOException;
}
