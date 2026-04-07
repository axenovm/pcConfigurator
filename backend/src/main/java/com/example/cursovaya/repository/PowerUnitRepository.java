package com.example.cursovaya.repository;

import com.example.cursovaya.entity.PowerUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerUnitRepository extends JpaRepository<PowerUnit, Long> {
}
