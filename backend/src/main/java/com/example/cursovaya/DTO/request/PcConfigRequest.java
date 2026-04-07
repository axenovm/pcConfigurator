package com.example.cursovaya.DTO.request;

import lombok.Data;

@Data
public class PcConfigRequest {
    private String name;

    private Long motherboardId;

    private Long pcCaseId;

    private Long powerUnitId;

    private Long processorId;

    private Long processorCoolingId;

    private Long userId;

    private Long videoCardId;
}
