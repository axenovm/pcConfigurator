package com.example.cursovaya.DTO.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PowerUnitResponse {
    private Long id;

    private String colour;

    private String model;

    private Integer power;

    private BigDecimal price;
}
