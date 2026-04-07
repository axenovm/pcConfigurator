package com.example.cursovaya.DTO.request;

import lombok.Data;

@Data
public class PcConfigStorageRequest {
    private Integer quantity;

    private Long pcConfigurationId;

    private Long storageDeviceId;
}
