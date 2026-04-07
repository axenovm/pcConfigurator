package com.example.cursovaya.DTO.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PcCaseResponse {
    private Long id;

    private String color;

    private String formFactor;

    private String model;

    private BigDecimal price;
}
