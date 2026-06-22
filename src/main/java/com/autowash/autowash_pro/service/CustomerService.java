package com.autowash.autowash_pro.service;

import com.autowash.autowash_pro.dto.request.CustomerRequestDTO;
import com.autowash.autowash_pro.dto.request.auth.ChangePasswordRequest;
import com.autowash.autowash_pro.dto.response.CustomerProfileResponse;
import com.autowash.autowash_pro.entity.Customer;
import com.autowash.autowash_pro.enums.Tier;
import com.autowash.autowash_pro.exception.BusinessException;
import com.autowash.autowash_pro.exception.ResourceNotFoundException;
import com.autowash.autowash_pro.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import com.autowash.autowash_pro.entity.Vehicle;
import com.autowash.autowash_pro.repository.VehicleRepository;
import com.autowash.autowash_pro.entity.Booking;
import com.autowash.autowash_pro.repository.BookingRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
                .fullName(dto.getFullName())
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
    private BookingRepository bookingRepository;

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

        customer.setFullName(dto.getFullName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        // Bạn có thể bổ sung thêm customer.setTier(...) nếu muốn cho Admin đổi hạng thẻ
        // tại đây

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
        return vehicleRepository.findByCustomer_CustomerId(customerId);
    }

    // Gọi hàm có sẵn của nhóm để lôi lịch sử đặt lịch rửa xe
    public List<Booking> getBookingHistoryByCustomerId(UUID customerId) {
        return bookingRepository.findByCustomer_CustomerId(customerId);
    }

    // Lấy thông tin profile của khách hàng hiện tại theo phone
    @Transactional(readOnly = true)
    public CustomerProfileResponse getMyProfile(String phone) {
        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Không tìm thấy tài khoản với số điện thoại: " + phone
                ));

        return mapToProfileResponse(customer);
    }

    @Transactional
    public CustomerProfileResponse updateMyProfile(String phone,
                                                   CustomerRequestDTO request) {
        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Không tìm thấy tài khoản với số điện thoại: " + phone
                ));

        if (!customer.getPhone().equals(request.getPhone())
                && customerRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Số điện thoại đã được sử dụng");
        }

        if (request.getEmail() != null
                && !request.getEmail().isBlank()
                && !request.getEmail().equals(customer.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email đã được sử dụng");
        }

        customer.setFullName(request.getFullName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());

        Customer savedCustomer = customerRepository.save(customer);
        return mapToProfileResponse(savedCustomer);
    }

    // Helper method - Convert Customer entity to CustomerProfileResponse
    private CustomerProfileResponse mapToProfileResponse(Customer customer) {
        return CustomerProfileResponse.builder()
                .customerId(customer.getCustomerId())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .tier(customer.getTier())
                .totalPoints(customer.getTotalPoints())
                .lifetimePoints(customer.getLifetimePoints())
                .totalVisits(customer.getTotalVisits())
                .totalSpend(customer.getTotalSpend())
                .registeredAt(customer.getRegisteredAt())
                .lastVisitAt(customer.getLastVisitAt())
                .isActive(customer.isActive())
                .build();
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest request) {
        Customer customer = getCustomerById(id);

        if (!passwordEncoder.matches(request.getOldPassword(), customer.getPassword())) {
            throw new BusinessException("Mật khẩu cũ không đúng");
        }

        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new BusinessException("Mật khẩu mới ít nhất 6 ký tự");
        }

        customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customerRepository.save(customer);
    }
}