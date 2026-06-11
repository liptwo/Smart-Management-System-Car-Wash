package com.autowash.autowash_pro.controller;

import com.autowash.autowash_pro.dto.request.CustomerRequestDTO;
import com.autowash.autowash_pro.entity.Customer;
import com.autowash.autowash_pro.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

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

    // 1. Endpoint: GET http://localhost:8080/api/admin/customers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    // 2. Endpoint: PUT http://localhost:8080/api/admin/customers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable UUID id, @RequestBody CustomerRequestDTO dto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }

    // 3. Endpoint: DELETE http://localhost:8080/api/admin/customers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> disableCustomer(@PathVariable UUID id) {
        customerService.disableCustomer(id);
        return ResponseEntity.ok("Đã vô hiệu hóa khách hàng thành công!");
    }
}