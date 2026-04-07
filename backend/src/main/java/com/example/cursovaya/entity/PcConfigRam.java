package com.example.cursovaya.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "pc_config_rams")
public class PcConfigRam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_ram_id")
    private Long configRamId;

    @Column(nullable = false)
    private Integer ramQuantity;

    @Column(nullable = false)
    private BigDecimal price;

    //связи

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "pcConfig_id")
    @ToString.Exclude
    private PcConfiguration pcConfiguration;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "ramModule_id")
    @ToString.Exclude
    private RamModule ramModule;

}
