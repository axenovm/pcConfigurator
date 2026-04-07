package com.example.cursovaya.DTO.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessorRequest {
    private String model;

    private String modelCode;

    private BigDecimal price;

    private String socket;
}
