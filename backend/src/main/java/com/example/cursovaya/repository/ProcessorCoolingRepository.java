package com.example.cursovaya.repository;

import com.example.cursovaya.entity.PowerUnit;
import com.example.cursovaya.entity.ProcessorCooling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessorCoolingRepository extends JpaRepository<ProcessorCooling, Long> {
}
