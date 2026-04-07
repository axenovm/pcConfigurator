package com.example.cursovaya.repository;

import com.example.cursovaya.entity.PcConfigStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PcConfigStorageRepository extends JpaRepository<PcConfigStorage, Long> {
    List<PcConfigStorage> findByPcConfigurationId(Long pcConfigurationId);
}
