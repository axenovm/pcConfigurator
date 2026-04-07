package com.example.cursovaya.DTO.response;

import com.example.cursovaya.enums.TypeCooling;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessorCoolingResponse {
    private Long id;

    private String modelCooling;

    private BigDecimal price;

    private TypeCooling typeCooling;
}
