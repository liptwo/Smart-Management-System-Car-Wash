package com.autowash.autowash_pro.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.autowash.autowash_pro.enums.Tier;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerProfileResponse {
    private UUID customerId;
    private String fullName;
    private String phone;
    private String email;
    private Tier tier;
    
    // Loyalty points
    private int totalPoints;
    private int lifetimePoints;
    
    // Statistics
    private int totalVisits;
    private BigDecimal totalSpend;
    
    // Timestamps
    private LocalDateTime registeredAt;
    private LocalDateTime lastVisitAt;
    
    // Status
    private boolean isActive;
}
