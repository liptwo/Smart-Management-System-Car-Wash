package com.autowash.autowash_pro.repository;

import com.autowash.autowash_pro.entity.Customer;
import com.autowash.autowash_pro.enums.Tier; // Đảm bảo import đầy đủ nếu dùng bên dưới
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    
    // 🛠️ ĐÂY LÀ HÀM BỊ THIẾU KHIẾN HỆ THỐNG BÁO LỖI:
    boolean existsByEmail(String email);

    // Các hàm còn lại bạn giữ nguyên như cũ:
    boolean existsByPhone(String phone);
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByEmail(String email);
    List<Customer> findByTier(Tier tier);
}