package com.autowash.autowash_pro.dto.request;

import java.util.UUID;

import com.autowash.autowash_pro.enums.RedeemType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedeemPointsRequest {

    @NotNull(message = "customerId không được để trống")
    private UUID customerId;

    @NotNull(message = "Loại redeem không được để trống")
    private RedeemType redeemType;

    @NotNull(message = "referenceId không được để trống")
    private UUID referenceId;
}
