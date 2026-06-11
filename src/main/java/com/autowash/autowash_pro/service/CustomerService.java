package com.autowash.autowash_pro.service;

import com.autowash.autowash_pro.dto.request.CustomerRequestDTO;
import com.autowash.autowash_pro.entity.Customer;
import com.autowash.autowash_pro.enums.Tier;
import com.autowash.autowash_pro.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // Lấy toàn bộ danh sách khách hàng từ database đám mây Supabase
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Tạo mới khách hàng trực tiếp tại quầy Admin
    public Customer createCustomerAtCounter(CustomerRequestDTO dto) {
        if (customerRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã tồn tại!");
        }

        Customer customer = Customer.builder()
                .fullName(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .password("$2a$10$X8A2M6fB8z7Gk9b3C2e1o.U9kZ5g6h7i8j9k1l2m3n4o5p6q7r8s9") // Mật khẩu mặc định 123456
                .tier(Tier.MEMBER) // Gán kiểu Enum chuẩn của bạn
                .totalPoints(0)
                .lifetimePoints(0)
                .totalVisits(0)
                .totalSpend(BigDecimal.ZERO)
                .isActive(true)
                .isAdmin(false)
                .build();
        
        return customerRepository.save(customer);
    }

    @Autowired
    private VehicleRepository vehicleRepository;

    // 1. Logic lấy chi tiết 1 khách hàng theo ID
    public Customer getCustomerById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));
    }

    // 2. Logic cập nhật thông tin khách hàng
    @Transactional
    public Customer updateCustomer(UUID id, CustomerRequestDTO dto) {
        Customer customer = getCustomerById(id);
        
        customer.setFullName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        // Bạn có thể bổ sung thêm customer.setTier(...) nếu muốn cho Admin đổi hạng thẻ tại đây

        return customerRepository.save(customer);
    }

    // 3. Logic vô hiệu hóa khách hàng (Xóa mềm)
    @Transactional
    public void disableCustomer(UUID id) {
        Customer customer = getCustomerById(id);
        customer.setActive(false);
        customerRepository.save(customer);
    }
    public List<Vehicle> getVehiclesByCustomerId(UUID customerId) {
       return vehicleRepository.findByCustomerCustomerId(customerId);
   }
}