package com.example.cursovaya.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "pc_config_storages")
public class PcConfigStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_storage_id")
    private Long configStorageId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcConfiguration_id", nullable = false)
    @ToString.Exclude
    private PcConfiguration pcConfiguration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storageDevice_id", nullable = false)
    @ToString.Exclude
    private StorageDevice storageDevice;
}
