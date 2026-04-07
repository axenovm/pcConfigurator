package com.example.cursovaya.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "power_units")
public class PowerUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "power_unit_id")
    private Long powerUnitId;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer power;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private BigDecimal price;

    //связи

    @OneToMany(mappedBy = "powerUnit")
    @ToString.Exclude
    private List<PcConfiguration> pcConfigurations;
}
