package com.autowash.autowash_pro.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autowash.autowash_pro.entity.Booking;
import com.autowash.autowash_pro.enums.BookingStatus;


@Repository
public interface BookingRepository
        extends JpaRepository<Booking, UUID> {

    List<Booking> findByCustomer_CustomerId(UUID customerId);
    List<Booking> findByCustomer_CustomerIdOrderByScheduledAtDesc(UUID customerId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByStatusOrderByScheduledAtAsc(BookingStatus status);
    List<Booking> findByCustomer_CustomerIdAndStatus(
        UUID customerId, BookingStatus status);

    // Đếm pending booking của customer
    int countByCustomer_CustomerIdAndStatus(
        UUID customerId, BookingStatus status);

    // Tìm booking theo ngày
    List<Booking> findByScheduledAtBetween(
        LocalDateTime from, LocalDateTime to);

    List<Booking> findByScheduledAtBetweenOrderByPriorityScoreDescCreatedAtAsc(
        LocalDateTime from, LocalDateTime to);

    int countByScheduledAtAndStatusIn(
        LocalDateTime scheduledAt, List<BookingStatus> statuses);
    boolean existsByVehicle_VehicleId(UUID vehicleId);
}
