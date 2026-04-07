package com.example.cursovaya.DTO.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VideoCardRequest {
    private String graphicsProcessor;

    private BigDecimal price;

    private String model;
}
