package com.example.cursovaya.repository;

import com.example.cursovaya.entity.PcCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PcCaseRepository extends JpaRepository<PcCase, Long> {
}
