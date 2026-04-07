package com.example.cursovaya.repository;

import com.example.cursovaya.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPcConfiguration_IdOrderByCreateAtDesc(Long pcConfigurationId);
}
