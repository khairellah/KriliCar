package com.kriliCar.dtos.responses;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CarImageDTO {
    private Long id;
    private String path;
    private Integer sortOrder;
}