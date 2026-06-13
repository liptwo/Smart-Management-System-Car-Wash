package com.autowash.autowash_pro.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.autowash.autowash_pro.entity.Vehicle;
import com.autowash.autowash_pro.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private CustomerService customerService;

    // Test Case 1: Kiểm thử độc lập logic lấy danh sách xe của Service
    @Test
    public void testGetVehiclesByCustomerId_ShouldReturnList() {
        UUID customerId = UUID.randomUUID();
        when(vehicleRepository.findByCustomer_CustomerId(customerId)).thenReturn(new ArrayList<>());

        List<Vehicle> vehicles = customerService.getVehiclesByCustomerId(customerId);
        
        assertNotNull(vehicles);
    }
}