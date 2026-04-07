package com.example.cursovaya.entity;

import com.example.cursovaya.enums.TypeMemory;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name ="ram_modules" )
public class RamModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ram_module_id")
    private Long ramModuleId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeMemory typeMemory;

    @Column(nullable = false)
    private String modelMemory;

    @Column(nullable = false)
    private Integer clockFrequency;

    @Column(nullable = false)
    private Integer capacityGb;

    @Column(nullable = false)
    private BigDecimal price;

    //связи

    @OneToMany(mappedBy = "ramModule")
    @ToString.Exclude
    private List<PcConfigRam> pcConfigRam;
}
