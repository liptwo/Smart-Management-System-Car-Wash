package com.autowash.autowash_pro.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autowash.autowash_pro.dto.request.auth.AuthResponse;
import com.autowash.autowash_pro.dto.request.auth.LoginRequest;
import com.autowash.autowash_pro.dto.request.auth.RefreshTokenRequest;
import com.autowash.autowash_pro.dto.request.auth.RegisterRequest;
import com.autowash.autowash_pro.dto.request.auth.AuthResponse;
import com.autowash.autowash_pro.dto.request.auth.RegisterRequest;
import com.autowash.autowash_pro.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Đăng ký, đăng nhập, refresh token")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản mới")
    public ResponseEntity<AuthResponse> register(
            @RequestBody @Valid RegisterRequest request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập bằng phone + password")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Lấy access token mới bằng refresh token")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
