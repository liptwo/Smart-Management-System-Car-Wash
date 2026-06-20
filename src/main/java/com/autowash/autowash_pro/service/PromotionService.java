package com.autowash.autowash_pro.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

import com.autowash.autowash_pro.entity.Promotion;
import com.autowash.autowash_pro.dto.request.PromotionRequest; 
import com.autowash.autowash_pro.repository.PromotionRepository;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;

    // Giữ nguyên Constructor Injection chuẩn chỉnh của bạn
    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    // 1. API LẤY DANH SÁCH (CŨ - GIỮ NGUYÊN)
    public List<Promotion> getPromotionsByStatus(String status) {
        // 1. Tab "Tất cả"
        if ("ALL".equalsIgnoreCase(status)) {
            return promotionRepository.findAll();
        }
        
        // 2. Tab "Đang chạy"
        if ("ACTIVE".equalsIgnoreCase(status)) {
            return promotionRepository.findActivePromotions(LocalDateTime.now());
        }
        
        // 3. Tab "Hết hạn"
        if ("EXPIRED".equalsIgnoreCase(status)) {
            return promotionRepository.findByIsActiveFalse();
        }
        
        return promotionRepository.findAll();
    }
    
    public Promotion createPromotion(PromotionRequest request) {
        Promotion promotion = new Promotion();
        
        promotion.setName(request.getName());
        
        // 1. Ép chuỗi String ("DISCOUNT" / "FREE_WASH") thành Enum tương ứng trong Entity
        if (request.getPromoType() != null) {
            promotion.setPromoType(com.autowash.autowash_pro.enums.PromoType.valueOf(request.getPromoType()));
        }
        
        // 2. Đổ BigDecimal an toàn
        promotion.setValue(request.getValue());
        
        // 3. Gọi đúng hàm camelCase chữ L viết hoa
        promotion.setUsageLimit(request.getUsageLimit()); 
        
        promotion.setTargetTiers(request.getTargetTiers());
        promotion.setStartsAt(request.getStartsAt());
        promotion.setEndsAt(request.getEndsAt());
        
        // 4. Sửa thành setActive (bỏ chữ is) theo đúng chuẩn Lombok cho trường boolean
        promotion.setActive(true); 
        
        promotion.setUsageCount(0);  
        promotion.setCreatedAt(LocalDateTime.now());
        
        return promotionRepository.save(promotion);
    }
}