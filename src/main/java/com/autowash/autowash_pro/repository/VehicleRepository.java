package com.autowash.autowash_pro.repository;

import com.autowash.autowash_pro.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    // Tìm danh sách xe dựa vào customerId của khách hàng liên kết
    List<Vehicle> findByCustomerCustomerId(UUID customerId);
}