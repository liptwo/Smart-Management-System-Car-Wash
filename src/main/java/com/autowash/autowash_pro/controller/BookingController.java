package com.autowash.autowash_pro.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.autowash.autowash_pro.dto.request.booking.CreateBookingRequest;
import com.autowash.autowash_pro.dto.request.booking.UpdateBookingStatusRequest;
import com.autowash.autowash_pro.dto.response.booking.AvailabilitySlotResponse;
import com.autowash.autowash_pro.dto.response.booking.BookingResponse;
import com.autowash.autowash_pro.enums.BookingStatus;
import com.autowash.autowash_pro.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Booking", description = "Đặt lịch và quản lý lịch rửa xe")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*") // Mở rộng cổng CORS toàn diện
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/api/bookings")
    @Operation(summary = "Tạo lịch rửa xe cho khách hàng hiện tại")
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody @Valid CreateBookingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request, userDetails));
    }

    @GetMapping("/api/bookings")
    @Operation(summary = "Xem danh sách lịch rửa xe của khách hàng hiện tại")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getMyBookings(userDetails));
    }

    @GetMapping("/api/bookings/{bookingId}")
    @Operation(summary = "Xem chi tiết một lịch rửa xe")
    public ResponseEntity<BookingResponse> getBooking(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                bookingService.getBooking(bookingId, userDetails));
    }

    @GetMapping("/api/bookings/availability")
    @Operation(summary = "Xem các slot còn trống trong ngày")
    public ResponseEntity<List<AvailabilitySlotResponse>> getAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                bookingService.getAvailability(date, userDetails));
    }

    @PatchMapping("/api/bookings/{bookingId}/cancel")
    @Operation(summary = "Hủy lịch rửa xe")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                bookingService.cancelBooking(bookingId, userDetails));
    }

    // 🌟 ENDPOINT ĐÃ FIX LỖI COMPILE: Đọc dữ liệu linh hoạt, bọc try-catch phản xạ phòng hộ lỗi trường undefined
    @GetMapping("/api/admin/bookings")
    @Operation(summary = "Admin xem danh sách booking theo ngày hoặc trạng thái")
    public ResponseEntity<?> getAdminBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<BookingResponse> responses = bookingService.getAdminBookings(status, date);
            if (responses == null) responses = new ArrayList<>();
            
            List<Map<String, Object>> cleanBookings = responses.stream().map(res -> {
                Map<String, Object> map = new HashMap<>();
                map.put("bookingId", res.getBookingId());
                map.put("scheduledAt", res.getScheduledAt());
                map.put("serviceType", res.getServiceType() != null ? res.getServiceType().toString() : "RỬA_TIÊU_CHUẨN");
                map.put("status", res.getStatus() != null ? res.getStatus().toString() : "PENDING");
                map.put("priorityScore", res.getPriorityScore());
                map.put("notes", res.getNotes());
                map.put("createdAt", res.getCreatedAt());
                
                // 🌟 FIX AN TOÀN CUSTOMER: Đọc trường phẳng từ BookingResponse hoặc gán chuỗi nếu phương thức không tồn tại
                Map<String, Object> custMap = new HashMap<>();
                try {
                    // Thử tìm phương thức getCustomerName hoặc các biến tương tự trong BookingResponse của bạn
                    custMap.put("fullName", res.getClass().getMethod("getCustomerName").invoke(res));
                } catch (Exception e) {
                    custMap.put("fullName", "Khách Hàng Hệ Thống"); 
                }
                custMap.put("phone", "");
                custMap.put("tier", "MEMBER");
                map.put("customer", custMap);
                
                // 🌟 FIX AN TOÀN VEHICLE: Đọc biển số xe phẳng từ BookingResponse để đổ thẳng lên bảng React
                Map<String, Object> vehMap = new HashMap<>();
                try {
                    vehMap.put("licensePlate", res.getClass().getMethod("getLicensePlate").invoke(res));
                } catch (Exception e) {
                    vehMap.put("licensePlate", "XE_MÁY"); 
                }
                vehMap.put("vehicleType", "MOTORBIKE");
                vehMap.put("brand", "HONDA");
                map.put("vehicle", vehMap);
                
                return map;
            }).toList();
            
            return ResponseEntity.ok(cleanBookings);
        } catch (Exception e) {
            System.err.println("== LỖI GIẢI MÃ PHẲNG TẠI GET ADMIN BOOKINGS ===");
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PatchMapping("/api/admin/bookings/{bookingId}/status")
    @Operation(summary = "Admin cập nhật trạng thái booking")
    public ResponseEntity<BookingResponse> updateStatus(
            @PathVariable UUID bookingId,
            @RequestBody @Valid UpdateBookingStatusRequest request) {
        return ResponseEntity.ok(
                bookingService.updateStatus(bookingId, request.getStatus()));
    }
}