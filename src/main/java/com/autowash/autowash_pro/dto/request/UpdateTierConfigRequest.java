package com.autowash.autowash_pro.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTierConfigRequest {

    @NotNull(message = "Giá trị VND cho mỗi điểm không được để trống")
    @Positive(message = "VND per point phải lớn hơn 0")
    private BigDecimal vndPerPoint;

    @Positive(message = "Multiplier member phải lớn hơn 0")
    private double multiplierMember;

    @Positive(message = "Multiplier silver phải lớn hơn 0")
    private double multiplierSilver;

    @Positive(message = "Multiplier gold phải lớn hơn 0")
    private double multiplierGold;

    @Positive(message = "Multiplier platinum phải lớn hơn 0")
    private double multiplierPlatinum;

    @Min(value = 0, message = "Thời hạn điểm phải lớn hơn hoặc bằng 0")
    private int pointExpiryMonths;

    @Min(value = 0, message = "Số điểm đổi 10k phải lớn hơn hoặc bằng 0")
    private int redeemPointsFor10k;

    @Min(value = 0, message = "Số điểm đổi dịch vụ Basic phải lớn hơn hoặc bằng 0")
    private int redeemPointsFreeBasic;

    @Min(value = 0, message = "Số điểm đổi dịch vụ Premium phải lớn hơn hoặc bằng 0")
    private int redeemPointsFreePremium;

    @Min(value = 0, message = "Số điểm đổi addon phải lớn hơn hoặc bằng 0")
    private int redeemPointsAddon;
}
