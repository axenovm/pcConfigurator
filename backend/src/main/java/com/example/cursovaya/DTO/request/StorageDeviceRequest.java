package com.example.cursovaya.DTO.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StorageDeviceRequest {
    private Integer hddCapacity;

    private String model;

    private BigDecimal price;
}
