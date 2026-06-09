package com.autowash.autowash_pro.dto.request;

import com.autowash.autowash_pro.enums.Tier;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String role;
    private Tier tier;
    private String fullName;
    private String phone;
}