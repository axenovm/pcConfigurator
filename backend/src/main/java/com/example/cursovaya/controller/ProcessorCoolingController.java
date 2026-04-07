package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.request.ProcessorCoolingRequest;
import com.example.cursovaya.DTO.response.ProcessorCoolingResponse;
import com.example.cursovaya.entity.ProcessorCooling;
import com.example.cursovaya.service.ProcessorCoolingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("processor-cooling")
public class ProcessorCoolingController {
    private final ProcessorCoolingService processorCoolingService;
    Logger log =  Logger.getLogger(ProcessorCoolingController.class.getName());

    public ProcessorCoolingController(ProcessorCoolingService processorCoolingservice){
        this.processorCoolingService=processorCoolingservice;
    }

    @GetMapping("{id}")
    public ResponseEntity<ProcessorCoolingResponse> getProcessorCoolingById(@PathVariable Long id){
        log.log(Level.INFO,"getProcessorCoolingById");
        return ResponseEntity.ok(processorCoolingService.getProcessorCoolingById(id));
    }

    @GetMapping()
    public ResponseEntity<List<ProcessorCoolingResponse>> getAllProcessorCooling(){
        log.log(Level.INFO,"getAllProcessorCooling");
        return ResponseEntity.ok(processorCoolingService.getAllProcessorCooling());
    }
}
