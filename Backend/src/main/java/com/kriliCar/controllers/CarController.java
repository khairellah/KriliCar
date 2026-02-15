package com.kriliCar.controllers;

import com.kriliCar.dtos.CarDTO;
import com.kriliCar.services.interfaces.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<CarDTO> createCar(
            @RequestPart("car") CarDTO carDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            Authentication authentication) throws IOException {
        return new ResponseEntity<>(carService.createCar(carDTO, imageFiles, authentication), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CarDTO>> getAllCars(
            @RequestParam(required = false) Long companyId,
            Pageable pageable) {
        return ResponseEntity.ok(carService.getAllCars(companyId, pageable));
    }

    @GetMapping("/{code}")
    public ResponseEntity<CarDTO> getCarByCode(@PathVariable String code) {
        return ResponseEntity.ok(carService.getCarByCode(code));
    }


    @PutMapping("/{code}")
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<CarDTO> updateCar(
            @PathVariable String code,
            @RequestBody CarDTO carDTO,
            Authentication authentication) {
        return ResponseEntity.ok(carService.updateCar(code, carDTO, authentication));
    }

    @DeleteMapping("/{code}")
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<Void> deleteCar(@PathVariable String code, Authentication authentication) {
        carService.deleteCar(code, authentication);
        return ResponseEntity.noContent().build();
    }
}