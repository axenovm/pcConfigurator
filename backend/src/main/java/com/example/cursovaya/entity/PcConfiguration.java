package com.example.cursovaya.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "pc_configurations")
public class PcConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pc_configuration_id")
    private Long id;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String buildName;

    //связи

    @OneToMany(mappedBy = "pcConfiguration")
    @ToString.Exclude
    private List<PcConfigRam> pcConfigRams;

    @OneToMany(mappedBy = "pcConfiguration")
    @ToString.Exclude
    private List<PcConfigStorage> pcConfigStorages;

    @OneToMany(mappedBy = "pcConfiguration")
    @ToString.Exclude
    private List<Review> reviews;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processor_id")
    @ToString.Exclude
    private Processor processor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcCase_id")
    @ToString.Exclude
    private PcCase pcCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motherboard_id")
    @ToString.Exclude
    private Motherboard motherboard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "powerUnit_id")
    @ToString.Exclude
    private PowerUnit powerUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "videoCard_id")
    @ToString.Exclude
    private VideoCard videoCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processorCooling_id")
    @ToString.Exclude
    private ProcessorCooling processorCooling;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;
}
