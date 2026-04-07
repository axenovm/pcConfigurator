package com.example.cursovaya.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "processors")
public class Processor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "processor_id")
    private Long processorId;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String modelCode;

    @Column(nullable = false)
    private String socket;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer tdp;

    //связи

    @OneToMany(mappedBy = "processor")
    @ToString.Exclude
    private List<PcConfiguration> pcConfigurations;

}
