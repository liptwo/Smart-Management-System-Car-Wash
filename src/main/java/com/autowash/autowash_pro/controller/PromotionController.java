package com.autowash.autowash_pro.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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

    // 1. API LẤY DANH SÁCH KHUYẾN MÃI (GET)
    @GetMapping
    public ResponseEntity<List<Promotion>> getAdminPromotions(
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status) {
        
        List<Promotion> promotions = promotionService.getPromotionsByStatus(status);
        return ResponseEntity.ok(promotions);
    }

    // 🌟 2. API TẠO MỚI CHIẾN DỊCH KHUYẾN MÃI (POST) - VÁ TRIỆT ĐỂ LỖI 405 METHOD NOT SUPPORTED
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
}