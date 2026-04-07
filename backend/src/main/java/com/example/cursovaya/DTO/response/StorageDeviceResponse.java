package com.example.cursovaya.DTO.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StorageDeviceResponse {
    private Long id;

    private Integer hddCapacity;

    private String model;

    private BigDecimal price;
}
