package com.example.cursovaya.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "storage_devices")
public class StorageDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_device_id")
    private Long storageDeviceId;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer HDDCapacity;

    @Column(nullable = false)
    private BigDecimal price;

    //связи

    @OneToMany(mappedBy = "storageDevice")
    @ToString.Exclude
    private List<PcConfigStorage> pcConfigStorages;
}
