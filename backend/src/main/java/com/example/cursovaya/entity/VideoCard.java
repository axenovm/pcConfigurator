package com.example.cursovaya.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "video_cards")
public class VideoCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_card_id")
    private Long videoCardId;

    @Column(nullable = false)
    private String videoCardModel;

    @Column(nullable = false)
    private String graphicsProcessor;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer tdp;

    @Column(nullable = false)
    private Integer length;

    //связи
    @OneToMany(mappedBy = "videoCard")
    @ToString.Exclude
    private List<PcConfiguration> pcConfigurations;
}
