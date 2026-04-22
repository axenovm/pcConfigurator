package com.example.cursovaya.repository;

import com.example.cursovaya.entity.PcConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PcConfigurationRepository extends JpaRepository<PcConfiguration, Long> {

    List<PcConfiguration> findByUserId(Long userId);

    List<PcConfiguration> findByPrivateBuildFalseOrPrivateBuildIsNull();
}
