package com.example.cursovaya.DTO.request;

import com.example.cursovaya.enums.TypeCooling;
import com.example.cursovaya.enums.TypeMemory;
import lombok.Data;
import org.springframework.web.bind.annotation.PatchMapping;

import java.lang.reflect.Type;
import java.math.BigDecimal;

@Data
public class RamModuleRequest {
    private Integer capacityGb;

    private Integer clockFrequency;

    private String modelMemory;

    private BigDecimal price;

    private TypeMemory typeMemory;
}
