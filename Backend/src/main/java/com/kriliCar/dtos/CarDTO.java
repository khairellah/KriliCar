package com.kriliCar.dtos;

import com.kriliCar.dtos.responses.CarImageDTO;
import com.kriliCar.enums.*;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CarDTO {
    private Long id; // Gardé pour la lecture, mais pas utilisé pour les liaisons en entrée
    private String code;
    private String vin;
    private Integer year;
    private Integer mileage;
    private Gearbox gearbox;
    private FuelType fuelType;
    private CarColor color;
    private String description;
    private Integer nbrSeats;
    private Double price;
    private CarAvailability availability;

    // Utilisation des codes métiers
    private String brandCode;
    private String modelCode;

    private String brandName;
    private String modelName;
    private Long companyId;

    private List<CarImageDTO> images;
}