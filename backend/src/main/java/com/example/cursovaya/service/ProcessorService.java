package com.example.cursovaya.service;

import com.example.cursovaya.DTO.response.ProcessorResponse;
import com.example.cursovaya.entity.Processor;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.ProcessorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class ProcessorService {
    private static final Logger log = Logger.getLogger(ProcessorService.class.getName());

    private final ProcessorRepository processorRepository;

    public ProcessorService(ProcessorRepository processorRepository) {

        this.processorRepository = processorRepository;
    }

    public ProcessorResponse getProcessorById(Long id){
        return convertToResponse(processorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Processor with id = " + id + "not found")));
    }

    public List<ProcessorResponse> getAllProcessors(){

        return processorRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    public ProcessorResponse convertToResponse(Processor processor){
        ProcessorResponse processorResponse = new ProcessorResponse();

        processorResponse.setId(processor.getProcessorId());
        processorResponse.setModel(processor.getModel());
        processorResponse.setModelCode(processor.getModelCode());
        processorResponse.setPrice(processor.getPrice());
        processorResponse.setSocket(processor.getSocket());

        return processorResponse;
    }
}
