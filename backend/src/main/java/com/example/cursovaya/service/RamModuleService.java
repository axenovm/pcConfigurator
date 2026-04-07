package com.example.cursovaya.service;

import com.example.cursovaya.DTO.response.RamModuleResponse;
import com.example.cursovaya.controller.RamModuleController;
import com.example.cursovaya.entity.RamModule;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.RamModuleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class RamModuleService {
    private final RamModuleRepository ramModuleRepository;

    private static final Logger logger = Logger.getLogger(RamModuleService.class.getName());

    public RamModuleService(RamModuleRepository ramModuleRepository) {
        this.ramModuleRepository = ramModuleRepository;
    }

    public RamModuleResponse getRamModuleBiId(Long id) {
        return convertToResponse(ramModuleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("RamModule with id = " + id + "not found")));
    }

    public List<RamModuleResponse> getAllRamModules(){

        return ramModuleRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    public RamModuleResponse convertToResponse(RamModule ramModule){
        RamModuleResponse ramModuleResponse = new RamModuleResponse();

        ramModuleResponse.setId(ramModule.getRamModuleId());
        ramModuleResponse.setCapacityGb(ramModule.getCapacityGb());
        ramModuleResponse.setClockFrequency(ramModule.getClockFrequency());
        ramModuleResponse.setModelMemory(ramModule.getModelMemory());
        ramModuleResponse.setPrice(ramModule.getPrice());
        ramModuleResponse.setTypeMemory(ramModule.getTypeMemory());

        return  ramModuleResponse;
    }
}
