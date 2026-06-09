package com.autowash.autowash_pro.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autowash.autowash_pro.entity.Customer;
import com.autowash.autowash_pro.enums.Tier;

@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByEmail(String email);
    
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    List<Customer> findByTier(Tier tier);
    List<Customer> findByTierIn(List<Tier> tiers);
}