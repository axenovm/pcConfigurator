package com.example.cursovaya.DTO.request;

import com.example.cursovaya.DTO.response.MotherboardResponse;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MotherboardRequest {
    private String colour;

    private String formFactor;

    private String model;

    private BigDecimal  price;

    private String series;

    private String socket;
}
