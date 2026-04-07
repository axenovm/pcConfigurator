package com.example.cursovaya.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "pc_cases")
public class PcCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pc_case_id")
    private Long pcCaseId;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String formFactor;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer maxGpuLength;

    //связи

    @OneToMany(mappedBy = "pcCase")
    @ToString.Exclude
    private List<PcConfiguration> pcConfigurations;
}
