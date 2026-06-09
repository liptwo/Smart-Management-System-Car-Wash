package com.autowash.autowash_pro.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.autowash.autowash_pro.entity.Promotion;

import io.lettuce.core.dynamic.annotation.Param;
@Repository
public interface PromotionRepository
        extends JpaRepository<Promotion, UUID> {

    List<Promotion> findByIsActiveTrue();

    // Tìm promo đang hoạt động và còn hạn
    @Query("SELECT p FROM Promotion p " +
           "WHERE p.isActive = true " +
           "AND p.startsAt <= :now " +
           "AND p.endsAt >= :now")
    List<Promotion> findActivePromotions(
        @Param("now") LocalDateTime now);
}