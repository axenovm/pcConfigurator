package com.example.cursovaya.DTO.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PowerUnitRequest {
    private String colour;

    private String model;

    private Integer power;

    private BigDecimal price;
}
