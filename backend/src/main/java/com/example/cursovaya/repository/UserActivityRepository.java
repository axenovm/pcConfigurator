package com.example.cursovaya.repository;

import com.example.cursovaya.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findByUser_IdOrderByTimestampDesc(Long userId);
}
