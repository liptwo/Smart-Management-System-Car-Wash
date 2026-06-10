package com.autowash.autowash_pro.controller;

import com.autowash.autowash_pro.dto.request.CustomerRequestDTO;
import com.autowash.autowash_pro.entity.Customer;
import com.autowash.autowash_pro.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/customers")
@CrossOrigin(origins = "http://localhost:5173") // Mở cổng cho React gọi sang
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Endpoint lấy danh sách khách hàng thật
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // Endpoint nhận dữ liệu từ Form popup dưới dạng JSON để lưu vào database
    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody CustomerRequestDTO request) {
        try {
            Customer createdCustomer = customerService.createCustomerAtCounter(request);
            return ResponseEntity.ok(createdCustomer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}