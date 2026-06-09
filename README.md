# AutoWash Pro

![AutoWash Pro Overview](https://via.placeholder.com/1200x400.png?text=AutoWash+Pro+Overview)

**AutoWash Pro** là hệ thống quản lý rửa xe máy thông minh, tích hợp đặt lịch trước, chương trình khách hàng thân thiết đa cấp và báo cáo vận hành cho admin.

---

## Tổng quan

- **Tên dự án:** AutoWash Pro
- **Mô tả:** Hệ thống quản lý rửa xe máy thông minh tích hợp đặt lịch trước và chương trình khách hàng thân thiết đa cấp.
- **Mục tiêu:** Tăng trải nghiệm khách hàng, thúc đẩy khả năng quay lại, quản lý vận hành hiệu quả cho tiệm rửa xe.

---

## Bối cảnh

Việt Nam có hơn 7.7 triệu xe máy, nhu cầu rửa xe tăng 25% mỗi năm. Khách hàng trung thành chi tiêu nhiều hơn 67% và ghé thăm thường xuyên hơn 3 lần. Hệ thống hiện tại thiếu phần thưởng cá nhân hóa, phân cấp thành viên và theo dõi điểm số kỹ thuật số.

---

## Người dùng chính

- **Customer**: chủ xe máy muốn đặt lịch và nhận thưởng.
- **Admin**: chủ/nhân viên tiệm rửa xe quản lý vận hành.

---

## Tính năng chính

![Booking Flow](https://via.placeholder.com/1000x300.png?text=Booking+Flow)

### Loyalty Engine

- Tích điểm theo tier
- Nâng/hạ hạng tự động hàng tháng
- Đổi điểm lấy thưởng
- Điểm hết hạn sau 12 tháng theo FIFO

### Booking System

- Đặt lịch với cửa sổ thời gian theo tier:
  - Member: 7 ngày
  - Silver: 10 ngày
  - Gold: 12 ngày
  - Platinum: 14 ngày
- Hàng ưu tiên theo tier
- Trạng thái booking: `PENDING → CONFIRMED → IN_PROGRESS → DONE → CANCELLED`

### Loyalty Tiers

- 4 cấp độ: `Member / Silver / Gold / Platinum`
- Tính dựa trên số lần rửa trong 12 tháng

### Customer Portal

- Xem điểm thưởng
- Lịch sử rửa xe
- Đặt lịch
- Quản lý xe
- Đổi thưởng
- Nhận khuyến mãi

### Admin Dashboard

- Quản lý booking
- Quản lý khách hàng
- Quản lý khuyến mãi theo tier
- Cấu hình tỷ lệ điểm
- Báo cáo doanh thu

### Notification

- Email OTP đăng nhập
- Xác nhận booking
- Thông báo điểm tích qua Resend
- Realtime status update qua WebSocket

---

## Kiến trúc kỹ thuật

![System Architecture](https://via.placeholder.com/1000x300.png?text=System+Architecture)

- **Backend:** Java 17 + Spring Boot 3.2
- **Database:** PostgreSQL (Supabase)
- **Cache:** Redis
- **Auth:** JWT + Email OTP
- **Email:** Resend.com
- **Realtime:** WebSocket (STOMP)
- **Storage:** Supabase Storage
- **Migration:** Flyway
- **Docs:** Swagger UI (springdoc-openapi)
- **Frontend:** React.js (repo riêng, kết nối qua CORS)
- **Deploy:** Docker + VPS + GitHub Actions CI/CD

---

## Cấu trúc project

```
src/main/java/com/autowash/autowashpro/
├── config/        JWT, Security, Redis, WebSocket, CORS, Swagger
├── controller/    Auth, Customer, Vehicle, Booking, Loyalty, Admin, Promotion
├── service/       Business logic từng module
├── repository/    Spring Data JPA interfaces
├── entity/        Customer, Vehicle, Booking, WashHistory, CustomerPoints, Promotion
├── dto/           Request + Response objects
├── enums/         Tier, BookingStatus, PointType, PromoType, ServiceType
└── exception/     GlobalExceptionHandler, custom exceptions
```

---

## Database chính

- `customers`
- `vehicles`
- `bookings`
- `wash_history`
- `customer_points`
- `promotions`

---

## API chính

```
POST /api/auth/send-otp
POST /api/auth/verify-otp
POST /api/auth/refresh

GET/POST/PUT  /api/customers
GET/POST      /api/customers/{id}/vehicles
GET/POST      /api/bookings
GET           /api/bookings/availability
PATCH         /api/bookings/{id}/cancel
PATCH         /api/bookings/{id}/status

POST          /api/loyalty/earn
POST          /api/loyalty/redeem
GET           /api/loyalty/balance/{id}

GET/POST/PUT  /api/admin/promotions
POST          /api/admin/promotions/{id}/send
GET/PUT       /api/admin/tier-config
GET           /api/admin/reports/revenue
GET           /api/admin/reports/customers
```

---

## Cài đặt dev

### Cách 1 — có Docker

```bash
docker-compose up -d
mvn spring-boot:run
```

### Cách 2 — không có Docker

```bash
# Trỏ application-dev.yml vào Supabase dev project
mvn spring-boot:run
```

---

## Biến môi trường cần thiết

```
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
REDIS_HOST
REDIS_PORT
RESEND_API_KEY
SUPABASE_URL
SUPABASE_ANON_KEY
```

---

## Team & phân công

- **Team:** 5 người, Scrum Lite 8 sprint / 16 tuần

### Phân công module

- Dev 1: Auth + Security + JWT
- Dev 2: Customer + Vehicle
- Dev 3: Booking + Notification
- Dev 4: Loyalty + Points + Scheduler
- Dev 5: Admin + Promotion + Reports

---

## Out of scope

- Thanh toán online
- Hoàn tiền
- Đa chi nhánh
- Nhận diện biển số (LPR)

---

## Ghi chú

- Đây là backend Spring Boot cho AutoWash Pro.
- Frontend React.js được triển khai riêng và kết nối qua API CORS.
- Sử dụng Swagger UI để xem tài liệu API khi chạy ứng dụng.
