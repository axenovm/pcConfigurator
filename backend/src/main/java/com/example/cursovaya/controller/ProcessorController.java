package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.response.ProcessorResponse;
import com.example.cursovaya.entity.Processor;
import com.example.cursovaya.service.ProcessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/processor")
public class ProcessorController {
    private static final Logger log = Logger.getLogger(ProcessorController.class.getName());

    private final ProcessorService processorService;

    public ProcessorController(ProcessorService processorService) {

        this.processorService = processorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessorResponse> getProcessorById(@PathVariable Long id){
        log.log(Level.INFO, "called method getProcessorById, with id = {}", id);
        ProcessorResponse processor = processorService.getProcessorById(id);
        return ResponseEntity.ok(processorService.getProcessorById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProcessorResponse>> getAllProcessors(){
        log.log(Level.INFO, "called method getAllProcessors");
        List<ProcessorResponse> processors = processorService.getAllProcessors();
        return ResponseEntity.ok(processorService.getAllProcessors());
    }

}
