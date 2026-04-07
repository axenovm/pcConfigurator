package com.example.cursovaya.DTO.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PcCaseRequest {
    private String color;

    private String formFactor;

    private String model;

    private BigDecimal price;
}
