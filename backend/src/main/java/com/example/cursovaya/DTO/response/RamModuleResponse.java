package com.example.cursovaya.DTO.response;

import com.example.cursovaya.enums.TypeCooling;
import com.example.cursovaya.enums.TypeMemory;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RamModuleResponse {
    private Long id;

    private Integer capacityGb;

    private Integer clockFrequency;

    private String modelMemory;

    private BigDecimal price;

    private TypeMemory typeMemory;
}
