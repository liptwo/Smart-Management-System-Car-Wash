package com.autowash.autowash_pro.controller;

import com.autowash.autowash_pro.dto.request.CustomerRequestDTO;
import com.autowash.autowash_pro.entity.Customer;
import com.autowash.autowash_pro.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import com.autowash.autowash_pro.entity.Vehicle;
import com.autowash.autowash_pro.entity.Booking;

@RestController
@RequestMapping("/api/admin/customers")
@CrossOrigin(origins = "http://localhost:5173") // Mở cổng cho React gọi sang
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Endpoint lấy danh sách khách hàng thật - Đã xử lý triệt tiêu vòng lặp JSON bằng DTO động
    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        
        List<Map<String, Object>> cleanResponse = customers.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("customerId", c.getCustomerId());
            map.put("fullName", c.getFullName());
            map.put("phone", c.getPhone());
            map.put("email", c.getEmail());
            map.put("tier", c.getTier() != null ? c.getTier().toString() : "MEMBER");
            map.put("totalPoints", c.getTotalPoints());
            map.put("totalVisits", c.getTotalVisits());
            map.put("isActive", c.isActive());
            
            // Ép cấu trúc phẳng cho danh sách xe, cắt bỏ mối liên kết ngược về Customer
            if (c.getVehicles() != null) {
                map.put("vehicles", c.getVehicles().stream().map(v -> {
                    Map<String, Object> vMap = new HashMap<>();
                    vMap.put("vehicleId", v.getVehicleId());
                    vMap.put("licensePlate", v.getLicensePlate());
                    vMap.put("brand", v.getBrand());
                    vMap.put("color", v.getColor());
                    vMap.put("vehicleType", v.getVehicleType());
                    return vMap;
                }).toList());
            }

            // Ép cấu trúc phẳng cho danh sách lịch hẹn, cắt bỏ mối liên kết ngược về Customer
            if (c.getBookings() != null) {
                map.put("bookings", c.getBookings().stream().map(b -> {
                    Map<String, Object> bMap = new HashMap<>();
                    bMap.put("bookingId", b.getBookingId());
                    bMap.put("serviceType", b.getServiceType());
                    bMap.put("status", b.getStatus());
                    bMap.put("scheduledAt", b.getScheduledAt());
                    return bMap;
                }).toList());
            }
            
            return map;
        }).toList();

        return ResponseEntity.ok(cleanResponse);
    }

    // Endpoint nhận dữ liệu từ Form popup dưới dạng JSON để lưu vào database
    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody CustomerRequestDTO request) {
        try {
            Customer createdCustomer = customerService.createCustomerAtCounter(request);
            return ResponseEntity.ok(createdCustomer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 1. Endpoint: GET http://localhost:8080/api/admin/customers/{id}
    // 🛠️ ĐÃ CẬP NHẬT: Phẳng hóa dữ liệu để tránh lỗi Nesting Depth tuần hoàn khi click xem chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable UUID id) {
        Customer c = customerService.getCustomerById(id);
        
        Map<String, Object> map = new HashMap<>();
        map.put("customerId", c.getCustomerId());
        map.put("fullName", c.getFullName());
        map.put("phone", c.getPhone());
        map.put("email", c.getEmail());
        map.put("tier", c.getTier() != null ? c.getTier().toString() : "MEMBER");
        map.put("totalPoints", c.getTotalPoints());
        map.put("totalVisits", c.getTotalVisits());
        map.put("isActive", c.isActive());
        
        return ResponseEntity.ok(map);
    }

    // 2. Endpoint: PUT http://localhost:8080/api/admin/customers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable UUID id, 
            @RequestParam(required = false) Boolean status,
            @RequestBody(required = false) CustomerRequestDTO dto) {
        
        Customer c;
        if (status != null) {
            customerService.toggleCustomerStatus(id, status);
            c = customerService.getCustomerById(id);
        } else {
            c = customerService.updateCustomer(id, dto);
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put("customerId", c.getCustomerId());
        map.put("fullName", c.getFullName());
        map.put("phone", c.getPhone());
        map.put("email", c.getEmail());
        map.put("tier", c.getTier() != null ? c.getTier().toString() : "MEMBER");
        map.put("totalPoints", c.getTotalPoints());
        map.put("totalVisits", c.getTotalVisits());
        map.put("isActive", c.isActive());
        
        return ResponseEntity.ok(map);
    }

    // 3. Endpoint: DELETE http://localhost:8080/api/admin/customers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> disableCustomer(@PathVariable UUID id) {
        customerService.disableCustomer(id);
        return ResponseEntity.ok("Đã vô hiệu hóa khách hàng thành công!");
    }

    // Endpoint: GET http://localhost:8080/api/admin/customers/{id}/vehicles
    // 🛠️ ĐÃ CẬP NHẬT: Ép dữ liệu xe về dạng Map phẳng thô, cắt đứt dây liên kết ngược về Customer Entity
    @GetMapping("/{id}/vehicles")
    public ResponseEntity<?> getVehiclesByCustomerId(@PathVariable UUID id) {
        List<Vehicle> vehicles = customerService.getVehiclesByCustomerId(id);
        
        List<Map<String, Object>> cleanVehicles = vehicles.stream().map(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put("vehicleId", v.getVehicleId());
            map.put("licensePlate", v.getLicensePlate());
            map.put("brand", v.getBrand());
            map.put("color", v.getColor());
            map.put("vehicleType", v.getVehicleType() != null ? v.getVehicleType().toString() : "CAR");
            return map;
        }).toList();
        
        return ResponseEntity.ok(cleanVehicles);
    }

    // Endpoint: GET http://localhost:8080/api/admin/customers/{id}/history
    // 🛠️ ĐÃ CẬP NHẬT: Ép dữ liệu lịch đặt về dạng Map phẳng thô, cắt đứt dây liên kết ngược về WashHistory/Customer
    @GetMapping("/{id}/history")
    public ResponseEntity<?> getBookingHistoryByCustomerId(@PathVariable UUID id) {
        List<Booking> bookings = customerService.getBookingHistoryByCustomerId(id);
        
        List<Map<String, Object>> cleanHistory = bookings.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("bookingId", b.getBookingId());
            map.put("serviceType", b.getServiceType() != null ? b.getServiceType().toString() : "Rửa Xe Cơ Bản");
            map.put("status", b.getStatus() != null ? b.getStatus().toString() : "HOAN_THANH");
            map.put("scheduledAt", b.getScheduledAt());
            return map;
        }).toList();
        
        return ResponseEntity.ok(cleanHistory);
    }
}