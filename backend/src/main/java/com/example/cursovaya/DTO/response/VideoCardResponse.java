package com.example.cursovaya.DTO.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VideoCardResponse {
    private Long id;

    private String graphicsProcessor;

    private BigDecimal price;

    private String model;
}
