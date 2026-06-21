package com.autowash.autowash_pro.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // 🌟 KÍCH HOẠT: Cấu hình bắt tay CORS hàng đầu hệ thống
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      
            .authorizeHttpRequests(auth -> auth
                // Thả tự do hoàn toàn cho mọi request OPTIONS kiểm tra của Chrome
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Mở cửa tự do hoàn toàn cho Dashboard endpoints công cộng
                .requestMatchers("/api/admin/dashboard/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()

                // Mở tự do luồng quản lý bài viết (Articles)
                .requestMatchers(HttpMethod.GET, "/api/admin/articles/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/admin/articles/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/admin/articles/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/admin/articles/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/admin/articles/**").permitAll()

                // Mở tự do luồng Promotions
                .requestMatchers(HttpMethod.PATCH, "/api/admin/promotions/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/admin/promotions/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/admin/promotions/**").permitAll()

                // Whitelist các đường dẫn mở tự do không cần Token
                .requestMatchers(
                    "/actuator/health",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/api/admin/customers/**",
                    "/api/admin/bookings/**",
                    "/api/bookings/**"
                ).permitAll()
                
                .requestMatchers(HttpMethod.PATCH, "/api/admin/**").permitAll()
                
                // Phân quyền khách hàng và admin dùng chung
                .requestMatchers(
                    "/api/customers/**",
                    "/api/loyalty/**",
                    "/api/vehicles/**"
                ).hasAnyRole("CUSTOMER", "ADMIN")
                
                // Các đường dẫn Admin còn lại bắt buộc quyền ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🌟 ĐÃ CẬP NHẬT: Cấu hình CORS cao cấp xử lý dứt điểm Preflight cho Chrome DevTools
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Chấp nhận mọi nguồn Frontend gọi tới một cách tường minh
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://127.0.0.1:5173"));
                
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // 🌟 FIX TRIỆT ĐỂ: Mở bung toàn bộ Headers, cho phép cả Authorization viết hoa/thường đi qua
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Link", "X-Total-Count"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // Cache cấu hình CORS trong 1 tiếng để không bị lặp lại request Preflight

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}