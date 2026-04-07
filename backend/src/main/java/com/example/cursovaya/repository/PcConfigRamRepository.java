package com.example.cursovaya.repository;

import com.example.cursovaya.entity.PcConfigRam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PcConfigRamRepository extends JpaRepository<PcConfigRam, Long> {

//    @Query("SELECT pcr FROM PcConfigRam pcr " +
//            "JOIN FETCH pcr.ramModule " +
//            "WHERE pcr.pcConfiguration.pcConfigurationId = :configId")
//    List<PcConfigRam> findByPcConfigurationIdWithModule(@Param("pcConfigurationId") Long pcConfigurationId);

    List<PcConfigRam> findByPcConfigurationId(Long pcConfigurationId);
}
