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
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      
            .authorizeHttpRequests(auth -> auth
        
                .requestMatchers(HttpMethod.PATCH, "/api/admin/promotions/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/admin/**").permitAll()
                
                // Mở cửa cho các phương thức GET và POST của promotions
                .requestMatchers(HttpMethod.GET, "/api/admin/promotions/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/admin/promotions/**").permitAll()

                // THÊM: Mở cửa thông suốt tự do cho các phương thức của quản lý bài viết (Articles)
                .requestMatchers(HttpMethod.GET, "/api/admin/articles/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/admin/articles/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/admin/articles/**").permitAll()

                // Whitelist các đường dẫn mở tự do (Không cần Token)
                .requestMatchers(
                    "/api/auth/**",
                    "/actuator/health",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/api/admin/customers/**",
                    "/api/admin/bookings/**",
                    "/api/bookings/**"
                ).permitAll()
                
                // Các đường dẫn Admin tổng quát còn lại công chứng quyền ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Phân quyền khách hàng và admin dùng chung
                .requestMatchers(
                    "/api/customers/**",
                    "/api/bookings/**",
                    "/api/loyalty/**",
                    "/api/vehicles/**"
                ).hasAnyRole("CUSTOMER", "ADMIN")
                
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Cấu hình danh sách tên miền Frontend được phép kết nối an toàn
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://127.0.0.1:5173"));
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}