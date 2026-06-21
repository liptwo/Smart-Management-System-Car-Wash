package com.autowash.autowash_pro.controller;

import com.autowash.autowash_pro.dto.DashboardStatsDTO;
import com.autowash.autowash_pro.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor 
// 🌟 ĐÃ NÂNG CẤP CORS: Cho phép đầy đủ các phương thức GET, POST, OPTIONS và truyền nhận Header bảo mật Authorization công khai
@CrossOrigin(
    origins = "http://localhost:5173", 
    allowedHeaders = "*", 
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS},
    allowCredentials = "true"
)
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            // Gọi hàm tính toán dữ liệu thực tế dynamic từ database của bạn
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("=== CRASH HỆ THỐNG TẠI CONTROLLER DASHBOARD ===");
            e.printStackTrace();
            
            // Nếu có lỗi kĩ thuật ngầm, trả về một Object rỗng mới để bảo vệ Frontend không bị sập giao diện
            return ResponseEntity.ok(new DashboardStatsDTO());
        }
    }
}