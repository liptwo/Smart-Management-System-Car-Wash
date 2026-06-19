package com.autowash.autowash_pro.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autowash.autowash_pro.dto.request.booking.CreateBookingRequest;
import com.autowash.autowash_pro.dto.response.booking.AvailabilitySlotResponse;
import com.autowash.autowash_pro.dto.response.booking.BookingResponse;
import com.autowash.autowash_pro.entity.Booking;
import com.autowash.autowash_pro.entity.Customer;
import com.autowash.autowash_pro.entity.Vehicle;
import com.autowash.autowash_pro.enums.BookingStatus;
import com.autowash.autowash_pro.enums.Tier;
import com.autowash.autowash_pro.exception.BusinessException;
import com.autowash.autowash_pro.exception.ResourceNotFoundException;
import com.autowash.autowash_pro.repository.BookingRepository;
import com.autowash.autowash_pro.repository.CustomerRepository;
import com.autowash.autowash_pro.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private static final LocalTime OPEN_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(18, 0);
    private static final int SLOT_MINUTES = 30;
    private static final int SLOT_CAPACITY = 2;
    private static final List<BookingStatus> ACTIVE_STATUSES = List.of(
        BookingStatus.PENDING,
        BookingStatus.CONFIRMED,
        BookingStatus.IN_PROGRESS
    );
    private static final Set<BookingStatus> TERMINAL_STATUSES = EnumSet.of(
        BookingStatus.DONE,
        BookingStatus.CANCELLED
    );

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final LoyaltyService loyaltyService;

    public BookingResponse createBooking(
            CreateBookingRequest request,
            UserDetails userDetails) {

        Customer customer;
        Vehicle vehicle;

        // Luồng 1: Khách hàng tự đặt lịch từ giao diện cá nhân
        if (userDetails != null) {
            customer = findCurrentCustomer(userDetails);
            vehicle = vehicleRepository
                .findByVehicleIdAndCustomer_CustomerId(
                    request.getVehicleId(), customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Không tìm thấy xe thuộc tài khoản hiện tại"));
        } else {
            // Luồng 2: POS ADMIN LÊN ĐƠN TẠI QUẦY (TỰ ĐỘNG BÓC TÁCH TẠO XE VÀ KHÁCH)
            String phoneTarget = null;
            String plateTarget = null;
            String modelTarget = null;

            if (request.getNotes() != null && request.getNotes().contains("Phone:")) {
                try {
                    // Cấu trúc chuỗi notes: POS Admin: Khách... | Phone:... | Biển số:... | Dòng xe:...
                    String[] parts = request.getNotes().split(" \\| ");
                    for (String part : parts) {
                        if (part.trim().startsWith("Phone:")) {
                            phoneTarget = part.replace("Phone:", "").trim();
                        } else if (part.trim().startsWith("Biển số:")) {
                            plateTarget = part.replace("Biển số:", "").trim();
                        } else if (part.trim().startsWith("Dòng xe:")) {
                            modelTarget = part.replace("Dòng xe:", "").trim();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[POS] Lỗi trích xuất thông tin chi tiết từ trường ghi chú notes");
                }
            }

            // 1. Định vị khách hàng mục tiêu qua Số điện thoại
            if (phoneTarget != null && !phoneTarget.isEmpty()) {
                customer = customerRepository.findByPhone(phoneTarget)
                    .orElseGet(() -> {
                        System.out.println("[POS] Số điện thoại lạ, gán tạm vào khách xe mặc định");
                        return vehicleRepository.findById(request.getVehicleId())
                                .map(Vehicle::getCustomer)
                                .orElse(null);
                    });
            } else {
                customer = vehicleRepository.findById(request.getVehicleId())
                        .map(Vehicle::getCustomer)
                        .orElse(null);
            }

            // 2. TỰ ĐỘNG KIỂM TRA BIỂN SỐ VÀ KHỞI TẠO THỰC THỂ XE THẬT DƯỚI DATABASE
            if (plateTarget != null && !plateTarget.isEmpty()) {
                final String finalPlate = plateTarget.toUpperCase().trim();
                final String finalBrand = (modelTarget != null && !modelTarget.isEmpty()) ? modelTarget : "POS Auto";
                final Customer finalCustomer = customer;

                // Tìm kiếm xe theo biển số đã được chuẩn hóa
                vehicle = vehicleRepository.findAll().stream()
                    .filter(v -> v.getLicensePlate() != null && v.getLicensePlate().toUpperCase().trim().equals(finalPlate))
                    .findFirst()
                    .orElseGet(() -> {
                        System.out.println("[POS] Xe mang biển số " + finalPlate + " chưa có trong hệ thống. Đang tạo mới...");
                        Vehicle newVehicle = Vehicle.builder()
                            .licensePlate(finalPlate)
                            .brand(finalBrand)
                            .color("Mặc định")
                            .vehicleType("CAR") 
                            .customer(finalCustomer)
                            .isPrimary(false)
                            .build();
                        return vehicleRepository.save(newVehicle);
                    });
            } else {
                // Phương án dự phòng bypass nếu lỗi trống biển số
                vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe hệ thống mặc định"));
            }
        }

        if (customer == null) {
            throw new BusinessException("Không xác định được khách hàng hợp lệ cho lịch đặt này");
        }

        LocalDateTime scheduledAt = request.getScheduledAt()
            .withSecond(0)
            .withNano(0);
        validateSchedulableSlot(customer, scheduledAt);

        Booking booking = Booking.builder()
            .customer(customer)
            .vehicle(vehicle)
            .scheduledAt(scheduledAt)
            .serviceType(request.getServiceType())
            .status(BookingStatus.PENDING)
            .priorityScore(customer.getTier().getPriorityScore())
            .notes(request.getNotes())
            .build();

        return mapToResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(UserDetails userDetails) {
        Customer customer = findCurrentCustomer(userDetails);
        return bookingRepository
            .findByCustomer_CustomerIdOrderByScheduledAtDesc(
                customer.getCustomerId())
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(UUID bookingId, UserDetails userDetails) {
        Booking booking = findBooking(bookingId);
        assertCanAccess(booking, userDetails);
        return mapToResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAdminBookings(
            BookingStatus status,
            LocalDate date) {

        if (date != null) {
            LocalDateTime from = date.atStartOfDay();
            LocalDateTime to = date.plusDays(1).atStartOfDay();
            return bookingRepository
                .findByScheduledAtBetweenOrderByPriorityScoreDescCreatedAtAsc(
                    from, to)
                .stream()
                .filter(booking -> status == null
                    || booking.getStatus() == status)
                .map(this::mapToResponse)
                .toList();
        }

        if (status != null) {
            return bookingRepository.findByStatusOrderByScheduledAtAsc(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
        }

        return bookingRepository.findAll().stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AvailabilitySlotResponse> getAvailability(
            LocalDate date,
            UserDetails userDetails) {

        Customer customer = findCurrentCustomer(userDetails);
        LocalDateTime firstSlot = date.atTime(OPEN_TIME);
        LocalDateTime closeAt = date.atTime(CLOSE_TIME);

        return Stream.iterate(
                firstSlot,
                slot -> slot.isBefore(closeAt),
                slot -> slot.plusMinutes(SLOT_MINUTES))
            .map(slot -> buildAvailabilitySlot(slot, customer))
            .toList();
    }

    public BookingResponse cancelBooking(
            UUID bookingId,
            UserDetails userDetails) {

        Booking booking = findBooking(bookingId);
        boolean admin = isAdmin(userDetails);
        if (!admin) {
            assertCanAccess(booking, userDetails);
        }

        if (booking.getStatus() == BookingStatus.DONE) {
            throw new BusinessException("Không thể hủy lịch đã hoàn tất");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return mapToResponse(booking);
        }
        if (!admin && booking.getStatus() == BookingStatus.IN_PROGRESS) {
            throw new BusinessException("Lịch đang rửa không thể tự hủy");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return mapToResponse(bookingRepository.save(booking));
    }

public BookingResponse updateStatus(
            UUID bookingId,
            BookingStatus newStatus) {

        Booking booking = findBooking(bookingId);
        BookingStatus oldStatus = booking.getStatus();
        
        if (oldStatus == BookingStatus.DONE) {
            return mapToResponse(booking);
        }
        
        validateStatusTransition(oldStatus, newStatus);
        booking.setStatus(newStatus);
        
        Booking savedBooking = bookingRepository.save(booking);

        if (oldStatus != BookingStatus.DONE && newStatus == BookingStatus.DONE) {
            if (savedBooking.getCustomer() != null) {
                
                BigDecimal amountPaid = BigDecimal.ZERO; 
                
                if (savedBooking.getServiceType() != null) {
                    amountPaid = BigDecimal.valueOf(savedBooking.getServiceType().getBasePrice());
                } else {
                    amountPaid = new BigDecimal("50000");
                }

                com.autowash.autowash_pro.dto.request.EarnPointsRequest earnRequest = 
                    new com.autowash.autowash_pro.dto.request.EarnPointsRequest();
                earnRequest.setCustomerId(savedBooking.getCustomer().getCustomerId());
                earnRequest.setWashId(savedBooking.getBookingId());
                earnRequest.setAmountPaid(amountPaid);

                // 1. Thực hiện tích điểm loyalty trước
                loyaltyService.earnPoints(earnRequest);
                
                // 2. Tự động thăng hạng thời gian thực sau khi nạp điểm thành công
                Customer currentCustomer = savedBooking.getCustomer();
                Customer updatedCustomer = customerRepository.findById(currentCustomer.getCustomerId()).orElse(currentCustomer);

                int currentPoints = updatedCustomer.getLifetimePoints(); 
                Tier newTier = Tier.getNextTierFromPoints(currentPoints);

                if (updatedCustomer.getTier() != newTier) {
                    updatedCustomer.setTier(newTier);
                    customerRepository.save(updatedCustomer);
                    System.out.println("[Promotion] Khách hàng " + updatedCustomer.getFullName() + " đã tự động thăng hạng lên: " + newTier);
                }
                
                System.out.println("[Automation] Đơn đặt lịch " + bookingId + " hoàn tất. Đã tự động tích điểm loyalty về đúng ID khách.");
            }
        }

        return mapToResponse(savedBooking);
    }

    private AvailabilitySlotResponse buildAvailabilitySlot(
            LocalDateTime slot,
            Customer customer) {

        int booked = bookingRepository
            .countByScheduledAtAndStatusIn(slot, ACTIVE_STATUSES);
        int remaining = Math.max(0, SLOT_CAPACITY - booked);
        
        boolean validForCustomer = isWithinBookingWindow(customer, slot)
            && slot.isAfter(LocalDateTime.now());

        return AvailabilitySlotResponse.builder()
            .startsAt(slot)
            .bookedCount(booked)
            .remainingCapacity(remaining)
            .available(validForCustomer && remaining > 0)
            .build();
    }

    private void validateSchedulableSlot(
            Customer customer,
            LocalDateTime scheduledAt) {

        if (scheduledAt.getMinute() % SLOT_MINUTES != 0
                || scheduledAt.getSecond() != 0) {
            throw new BusinessException(
                "Slot đặt lịch phải theo mốc 30 phút");
        }

        LocalTime time = scheduledAt.toLocalTime();
        if (time.isBefore(OPEN_TIME) || !time.isBefore(CLOSE_TIME)) {
            throw new BusinessException(
                "Chỉ nhận lịch trong khung 08:00 - 18:00");
        }

        if (!scheduledAt.isAfter(LocalDateTime.now().plusMinutes(30))) {
            throw new BusinessException(
                "Vui lòng đặt lịch trước ít nhất 30 phút");
        }

        if (!isWithinBookingWindow(customer, scheduledAt)) {
            throw new BusinessException(String.format(
                "Hạng %s chỉ được đặt lịch trong %d ngày tới",
                customer.getTier(),
                customer.getTier().getBookingWindowDays()));
        }

        int booked = bookingRepository
            .countByScheduledAtAndStatusIn(scheduledAt, ACTIVE_STATUSES);
        if (booked >= SLOT_CAPACITY) {
            throw new BusinessException(
                "Khung giờ này đã hết chỗ, vui lòng chọn slot khác");
        }
    }

    private boolean isWithinBookingWindow(
            Customer customer,
            LocalDateTime scheduledAt) {
        return !scheduledAt.isAfter(
            LocalDateTime.now()
                .plusDays(customer.getTier().getBookingWindowDays()));
    }

    private void validateStatusTransition(
            BookingStatus currentStatus,
            BookingStatus newStatus) {

        if (currentStatus == newStatus) {
            return;
        }
        if (TERMINAL_STATUSES.contains(currentStatus)) {
            throw new BusinessException(
                "Không thể đổi trạng thái của booking đã kết thúc");
        }

        boolean valid = switch (currentStatus) {
            case PENDING -> newStatus == BookingStatus.CONFIRMED
                || newStatus == BookingStatus.CANCELLED;
            case CONFIRMED -> newStatus == BookingStatus.IN_PROGRESS
                || newStatus == BookingStatus.CANCELLED;
            case IN_PROGRESS -> newStatus == BookingStatus.DONE;
            default -> false;
        };

        if (!valid) {
            throw new BusinessException(String.format(
                "Không thể đổi trạng thái từ %s sang %s",
                currentStatus, newStatus));
        }
    }

    private Booking findBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Không tìm thấy booking: " + bookingId));
    }

    private Customer findCurrentCustomer(UserDetails userDetails) {
        return customerRepository.findByPhone(userDetails.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Không tìm thấy tài khoản hiện tại"));
    }

    private void assertCanAccess(
            Booking booking,
            UserDetails userDetails) {

        if (isAdmin(userDetails)) {
            return;
        }

        Customer customer = findCurrentCustomer(userDetails);
        if (!booking.getCustomer().getCustomerId()
                .equals(customer.getCustomerId())) {
            throw new BusinessException(
                "Bạn không có quyền truy cập booking này");
        }
    }

    private boolean isAdmin(UserDetails userDetails) {
        if (userDetails == null) return false;
        return userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch("ROLE_ADMIN"::equals);
    }

    private BookingResponse mapToResponse(Booking booking) {
        Customer customer = booking.getCustomer();
        Vehicle vehicle = booking.getVehicle();
        
        String vType = "CAR";
        if (vehicle.getVehicleType() != null) {
            vType = vehicle.getVehicleType().toString();
        }

        return BookingResponse.builder()
            .bookingId(booking.getBookingId())
            .customerId(customer.getCustomerId())
            .customerName(customer.getFullName())
            .customerPhone(customer.getPhone())
            .customerTier(customer.getTier())
            .vehicleId(vehicle.getVehicleId())
            .licensePlate(vehicle.getLicensePlate())
            .vehicleType(vType)
            .scheduledAt(booking.getScheduledAt())
            .serviceType(booking.getServiceType())
            .basePrice(booking.getServiceType().getBasePrice())
            .status(booking.getStatus())
            .priorityScore(booking.getPriorityScore())
            .notes(booking.getNotes())
            .build();
    }
}