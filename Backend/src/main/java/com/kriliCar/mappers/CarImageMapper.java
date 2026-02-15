package com.kriliCar.mappers;

import com.kriliCar.dtos.responses.CarImageDTO;
import com.kriliCar.entities.CarImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarImageMapper {
    CarImageDTO toDTO(CarImage carImage);

    @Mapping(target = "car", ignore = true)
    CarImage toEntity(CarImageDTO carImageDTO);
}