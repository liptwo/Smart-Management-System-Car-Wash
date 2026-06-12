package com.autowash.autowash_pro.dto.response.booking;

import java.time.LocalDateTime;
import java.util.UUID;

import com.autowash.autowash_pro.enums.BookingStatus;
import com.autowash.autowash_pro.enums.ServiceType;
import com.autowash.autowash_pro.enums.Tier;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingResponse {

    private UUID bookingId;
    private UUID customerId;
    private String customerName;
    private String customerPhone;
    private Tier customerTier;
    private UUID vehicleId;
    private String licensePlate;
    private String vehicleType;
    private LocalDateTime scheduledAt;
    private ServiceType serviceType;
    private int basePrice;
    private BookingStatus status;
    private int priorityScore;
    private String notes;
    private LocalDateTime createdAt;
}
