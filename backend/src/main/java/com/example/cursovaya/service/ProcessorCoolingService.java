package com.example.cursovaya.service;

import com.example.cursovaya.DTO.response.ProcessorCoolingResponse;
import com.example.cursovaya.controller.ProcessorCoolingController;
import com.example.cursovaya.entity.ProcessorCooling;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.ProcessorCoolingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class ProcessorCoolingService {
    Logger log = Logger.getLogger(ProcessorCoolingService.class.getName());
    private final ProcessorCoolingRepository processorCoolingRepository;

    public ProcessorCoolingService(ProcessorCoolingRepository processorCoolingRepository) {
        this.processorCoolingRepository = processorCoolingRepository;
    }

    public ProcessorCoolingResponse getProcessorCoolingById(Long id) {
        return convertToResponse(processorCoolingRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("not found processor cooling with id = " + id)));
    }

    public List<ProcessorCoolingResponse> getAllProcessorCooling() {

        return processorCoolingRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    public ProcessorCoolingResponse convertToResponse(ProcessorCooling processorCooling) {
        ProcessorCoolingResponse processorCoolingResponse = new ProcessorCoolingResponse();

        processorCoolingResponse.setId(processorCooling.getProcessorCoolingId());
        processorCoolingResponse.setModelCooling(processorCooling.getModelCooling());
        processorCoolingResponse.setPrice(processorCooling.getPrice());
        processorCoolingResponse.setTypeCooling(processorCooling.getTypeCooling());
        return processorCoolingResponse;
    }
}
