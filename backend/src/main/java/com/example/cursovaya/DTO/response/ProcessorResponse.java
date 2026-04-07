package com.example.cursovaya.DTO.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessorResponse {
    private Long id;

    private String model;

    private String modelCode;

    private BigDecimal price;

    private String socket;
}
