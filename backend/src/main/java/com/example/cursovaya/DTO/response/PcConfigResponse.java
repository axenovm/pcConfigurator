package com.example.cursovaya.DTO.response;

import ch.qos.logback.core.joran.sanity.Pair;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PcConfigResponse {
    private Long id;

    private String name;

    private BigDecimal totalPrice;

    private String motherboardModel;

    private String pcCaseModel;

    private String powerUnitModel;

    private String processorModel;

    private String processorCoolingModel;

    private String userName;

    private String videoCardModel;

    @JsonProperty("isPrivate")
    private boolean privateBuild;

    private List<PcConfigRamResponse> ramModules;

    private List<PcConfigStorageResponse> storageDevices;
}
