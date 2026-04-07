package com.example.cursovaya.service;

import com.example.cursovaya.DTO.request.PcCaseRequest;
import com.example.cursovaya.DTO.response.PcCaseResponse;
import com.example.cursovaya.entity.PcCase;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.PcCaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PcCaseService {
    private final PcCaseRepository pcCaseRepository;

    public PcCaseService(PcCaseRepository pcCaseRepository) {
        this.pcCaseRepository = pcCaseRepository;
    }

    public PcCaseResponse getPcCaseById(Long id) {
        return convertToResponse(pcCaseRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("PcCase with id = " + id + "not found")));
    }

    public List<PcCaseResponse> getAllPcCase() {

        return pcCaseRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    public PcCaseResponse convertToResponse(PcCase pcCase) {
        PcCaseResponse pcCaseResponse = new PcCaseResponse();

        pcCaseResponse.setId(pcCase.getPcCaseId());
        pcCaseResponse.setColor(pcCase.getColor());
        pcCaseResponse.setFormFactor(pcCase.getFormFactor());
        pcCaseResponse.setModel(pcCase.getModel());
        pcCaseResponse.setPrice(pcCase.getPrice());

        return pcCaseResponse;
    }
}
