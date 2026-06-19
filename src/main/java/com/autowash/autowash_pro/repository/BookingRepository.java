package com.autowash.autowash_pro.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph; // 🌟 Thêm import này
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autowash.autowash_pro.entity.Booking;
import com.autowash.autowash_pro.enums.BookingStatus;

@Repository
public interface BookingRepository
        extends JpaRepository<Booking, UUID> {

    // Thêm EntityGraph để gom cụm quan hệ Customer và Vehicle khi tìm theo ID khách
    @EntityGraph(attributePaths = {"customer", "vehicle"})
    List<Booking> findByCustomer_CustomerId(UUID customerId);

    @EntityGraph(attributePaths = {"customer", "vehicle"})
    List<Booking> findByCustomer_CustomerIdOrderByScheduledAtDesc(UUID customerId);

    @EntityGraph(attributePaths = {"customer", "vehicle"})
    List<Booking> findByStatus(BookingStatus status);

    @EntityGraph(attributePaths = {"customer", "vehicle"})
    List<Booking> findByStatusOrderByScheduledAtAsc(BookingStatus status);

    @EntityGraph(attributePaths = {"customer", "vehicle"})
    List<Booking> findByCustomer_CustomerIdAndStatus(
        UUID customerId, BookingStatus status);

    // Đếm pending booking của customer (Hàm đếm số lượng thì giữ nguyên, không cần Join)
    int countByCustomer_CustomerIdAndStatus(
        UUID customerId, BookingStatus status);

    @EntityGraph(attributePaths = {"customer", "vehicle"})
    List<Booking> findByScheduledAtBetween(
        LocalDateTime from, LocalDateTime to);

    @EntityGraph(attributePaths = {"customer", "vehicle"})
    List<Booking> findByScheduledAtBetweenOrderByPriorityScoreDescCreatedAtAsc(
        LocalDateTime from, LocalDateTime to);

    int countByScheduledAtAndStatusIn(
        LocalDateTime scheduledAt, List<BookingStatus> statuses);

    boolean existsByVehicle_VehicleId(UUID vehicleId);
}