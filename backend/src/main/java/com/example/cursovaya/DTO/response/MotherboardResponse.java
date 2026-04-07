package com.example.cursovaya.DTO.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MotherboardResponse {
    private Long id;

    private String colour;

    private String formFactor;

    private String model;

    private BigDecimal price;

    private String series;

    private String socket;
}
