package com.example.cursovaya.entity;

import com.example.cursovaya.enums.TypeCooling;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "processors_cooling")
public class ProcessorCooling {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "processor_cooling_id")
    private Long processorCoolingId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeCooling typeCooling;

    @Column(nullable = false)
    private String modelCooling;

    @Column(nullable = false)
    private BigDecimal price;

    //связи

    @OneToMany(mappedBy = "processorCooling")
    @ToString.Exclude
    private List<PcConfiguration> pcConfigurations;
}
