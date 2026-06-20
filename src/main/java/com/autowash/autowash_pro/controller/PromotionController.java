package com.autowash.autowash_pro.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autowash.autowash_pro.entity.Promotion;
import com.autowash.autowash_pro.dto.request.PromotionRequest;
import com.autowash.autowash_pro.service.PromotionService;

@RestController
@RequestMapping("/api/admin/promotions")
@CrossOrigin(origins = "http://localhost:5173") // Cấu hình port mặc định của Vite Frontend
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    // 1. 🌟 API LẤY DANH SÁCH KHUYẾN MÃI ĐÃ ĐỒNG BỘ THAM SỐ TÌM KIẾM (GET)
    @GetMapping
    public ResponseEntity<List<Promotion>> getAdminPromotions(
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(value = "search", required = false) String search) { // 🌟 Nhận diện từ khóa gõ từ Topbar
        
        // Truyền cả trạng thái tab và từ khóa tìm kiếm xuống tầng nghiệp vụ Service
        List<Promotion> promotions = promotionService.getPromotionsByStatus(status, search);
        return ResponseEntity.ok(promotions);
    }

    // 2. API TẠO MỚI PROMOTION (POST)
    @PostMapping
    public ResponseEntity<?> createPromotion(@RequestBody PromotionRequest request) {
        try {
            // Gọi Service xử lý map dữ liệu và save xuống Postgres
            Promotion newPromo = promotionService.createPromotion(request);
            return ResponseEntity.ok(newPromo);
        } catch (Exception e) {
            // Trả về lỗi 400 kèm thông báo chi tiết nếu quá trình parse hoặc lưu DB thất bại
            return ResponseEntity.badRequest().body("Lỗi thêm khuyến mãi: " + e.getMessage());
        }
    }
    
    // 3. API ĐỔI TRẠNG THÁI HOẠT ĐỘNG (PATCH)
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> togglePromotionStatus(@PathVariable java.util.UUID id) {
        try {
            Promotion updatedPromo = promotionService.togglePromotionStatus(id);
            return ResponseEntity.ok(updatedPromo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi đổi trạng thái: " + e.getMessage());
        }
    }

    // 4. API KÍCH HOẠT GỬI THÔNG BÁO KHUYẾN MÃI (POST)
    @PostMapping("/{id}/send")
    public ResponseEntity<String> sendPromotionToTargets(@PathVariable java.util.UUID id) {
        try {
            // Gọi service xử lý logic lọc khách hàng và in log/gửi notification
            promotionService.sendPromotionToTargetUsers(id);
            return ResponseEntity.ok("Chiến dịch gửi thông báo khuyến mãi đã được kích hoạt thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi kích hoạt gửi ưu đãi: " + e.getMessage());
        }
    }
}