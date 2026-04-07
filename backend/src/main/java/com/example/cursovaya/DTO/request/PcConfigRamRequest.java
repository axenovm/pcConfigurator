package com.example.cursovaya.DTO.request;

import lombok.Data;

@Data
public class PcConfigRamRequest {
    private Integer ramQuantity;

    private Long pcConfigurationId;

    private Long ramModuleId;
}