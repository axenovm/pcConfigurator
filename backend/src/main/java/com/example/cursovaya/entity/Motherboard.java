package com.example.cursovaya.entity;

import com.example.cursovaya.enums.TypeMemory;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "motherboards")
public class Motherboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "motherboard_id")
    private Long motherboardId;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String series;

    @Column(nullable = false)
    private String formFactor;

    @Column(nullable = false)
    private String colour;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String socket;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeMemory supportedRamType;

    @Column(nullable = false)
    private Integer maxRamSlots;

    //связи

    @OneToMany(mappedBy = "motherboard")
    @ToString.Exclude
    private List<PcConfiguration> pcConfigurations;
}
