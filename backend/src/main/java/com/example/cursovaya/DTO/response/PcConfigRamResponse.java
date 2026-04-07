package com.example.cursovaya.DTO.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PcConfigRamResponse {
    private Long id;

    private BigDecimal price;

    private Integer quantity;

    private Long pcConfigurationId;

    private String moduleName;
}
