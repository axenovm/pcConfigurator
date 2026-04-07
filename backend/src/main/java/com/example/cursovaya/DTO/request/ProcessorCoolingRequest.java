package com.example.cursovaya.DTO.request;

import com.example.cursovaya.enums.TypeCooling;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessorCoolingRequest {
    private String modelCooling;

    private BigDecimal price;

    private TypeCooling typeCooling;
}
