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

    public static BookingResponse from(com.autowash.autowash_pro.entity.Booking booking) {
        com.autowash.autowash_pro.entity.Customer customer = booking.getCustomer();
        com.autowash.autowash_pro.entity.Vehicle vehicle = booking.getVehicle();
        
        String vType = "CAR";
        if (vehicle != null && vehicle.getVehicleType() != null) {
            vType = vehicle.getVehicleType().toString();
        }

        return BookingResponse.builder()
            .bookingId(booking.getBookingId())
            .customerId(customer != null ? customer.getCustomerId() : null)
            .customerName(customer != null ? customer.getFullName() : null)
            .customerPhone(customer != null ? customer.getPhone() : null)
            .customerTier(customer != null ? customer.getTier() : null)
            .vehicleId(vehicle != null ? vehicle.getVehicleId() : null)
            .licensePlate(vehicle != null ? vehicle.getLicensePlate() : null)
            .vehicleType(vType)
            .scheduledAt(booking.getScheduledAt())
            .serviceType(booking.getServiceType())
            .basePrice(booking.getServiceType() != null ? booking.getServiceType().getBasePrice() : 0)
            .status(booking.getStatus())
            .priorityScore(booking.getPriorityScore())
            .notes(booking.getNotes())
            .createdAt(booking.getCreatedAt())
            .build();
    }
}
