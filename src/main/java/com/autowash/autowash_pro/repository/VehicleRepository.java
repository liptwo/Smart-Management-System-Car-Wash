package com.autowash.autowash_pro.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autowash.autowash_pro.entity.Vehicle;

@Repository
public interface VehicleRepository
        extends JpaRepository<Vehicle, UUID> {

    List<Vehicle> findByCustomer_CustomerId(UUID customerId);
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    boolean existsByLicensePlate(String licensePlate);
    Optional<Vehicle> findByCustomer_CustomerIdAndIsPrimaryTrue(UUID customerId);
}