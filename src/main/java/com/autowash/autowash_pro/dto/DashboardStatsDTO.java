package com.autowash.autowash_pro.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private BigDecimal todayRevenue;
    private long totalWashCount;
    private long motorbikeCount;
    private long carCount;
    private long newCustomerCount;
    private long issuedPoints;
    
    // Đồng bộ với các class phụ trợ xịn của DashboardService
    private List<RevenueChartData> revenue7Days;
    private List<QueueDataDTO> todayQueue;
    
    private long memberCount;
    private long silverCount;
    private long goldCount;
    private long platinumCount;
}