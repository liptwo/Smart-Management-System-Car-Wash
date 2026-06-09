package com.autowash.autowash_pro.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.autowash.autowash_pro.entity.CustomerPoints;
import com.autowash.autowash_pro.enums.PointType;

import io.lettuce.core.dynamic.annotation.Param;
@Repository
public interface CustomerPointsRepository
        extends JpaRepository<CustomerPoints, UUID> {

    List<CustomerPoints> findByCustomer_CustomerIdOrderByCreatedAtDesc(
        UUID customerId);

    // Tìm điểm sắp hết hạn (dùng cho cron job)
    List<CustomerPoints> findByExpiresAtBeforeAndType(
        LocalDateTime dateTime, PointType type);

    // Tổng điểm EARN chưa hết hạn
    @Query("SELECT COALESCE(SUM(cp.points), 0) FROM CustomerPoints cp " +
           "WHERE cp.customer.customerId = :customerId " +
           "AND cp.type = 'EARN' " +
           "AND cp.expiresAt > :now")
    int sumActivePoints(
        @Param("customerId") UUID customerId,
        @Param("now") LocalDateTime now);
}