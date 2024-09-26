# 🐟 Koi Express - Hệ Thống Quản Lý Vận Chuyển Cá Koi

**Koi Express** là hệ thống vận chuyển cá Koi chuyên nghiệp, cung cấp dịch vụ vận chuyển cá Koi trong nước với các tiêu chuẩn an toàn cao nhất. Hệ thống hỗ trợ khách hàng trong toàn bộ quy trình, từ khi đặt đơn, tính toán chi phí, kiểm tra sức khỏe cá, đến theo dõi quá trình vận chuyển và nhận phản hồi sau khi giao hàng.

![Koi Express](./assets/images/Logo.jpg)

---

## 🚀 Tính Năng Chính

- **Quản Lý Đơn Hàng Vận Chuyển**: Khách hàng có thể đặt đơn vận chuyển Koi cá một cách dễ dàng, cung cấp đầy đủ thông tin về loại cá, trọng lượng, và kích thước.
- **Tính Toán Chi Phí Vận Chuyển**: Hệ thống tự động tính toán chi phí dựa trên các yếu tố như trọng lượng, kích thước, khoảng cách vận chuyển, và các dịch vụ bổ sung như bảo hiểm, chăm sóc đặc biệt, kiểm tra sức khỏe.
- **Theo Dõi Trạng Thái Vận Chuyển**: Người dùng và nhân viên có thể theo dõi trạng thái đơn hàng theo thời gian thực, từ quá trình đóng gói, vận chuyển đến khi giao hàng.
- **Quản Lý Nhân Sự**: Hệ thống phân quyền rõ ràng với nhiều vai trò như Khách hàng, Nhân viên chăm sóc khách hàng, Nhân viên giao hàng, và Quản lý.

- **Phản Hồi và Báo Cáo**: Khách hàng có thể gửi phản hồi về dịch vụ và giao diện hệ thống giúp các quản lý dễ dàng xem báo cáo về hoạt động giao hàng, hiệu quả nhân viên và phản hồi của khách hàng.

---

## 🛠️ Công Nghệ Sử Dụng

- **Frontend**:

  - ReactJS - Framework JavaScript mạnh mẽ giúp xây dựng giao diện người dùng hiện đại.
  - Vite - Công cụ build cực nhanh cho các ứng dụng web.
  - Tailwind CSS: Framework CSS utility-first giúp tạo giao diện đẹp và dễ dàng tùy chỉnh, đảm bảo tính nhất quán trong thiết kế và responsive trên mọi thiết bị.
  - Axios: Thư viện giúp xử lý các yêu cầu HTTP dễ dàng và linh hoạt, được sử dụng để gọi các API cho việc lấy dữ liệu đơn hàng, giá cả, và trạng thái vận chuyển.
  - React Router: Quản lý các routes trong ứng dụng, cung cấp điều hướng linh hoạt giữa các trang như trang đặt hàng, trạng thái vận chuyển, và hồ sơ khách hàng.

- **Backend**:

  - Spring Boot - Framework mạnh mẽ giúp xây dựng API RESTful và các dịch vụ backend.
  - MySQL - Cơ sở dữ liệu mạnh mẽ dùng để lưu trữ thông tin khách hàng, đơn hàng, và quản lý vận chuyển.
  - JWT (JSON Web Token): Được sử dụng để xác thực người dùng, đảm bảo tính bảo mật trong các yêu cầu API bằng cách mã hóa và kiểm tra quyền truy cập với các token JSON an toàn.

- **Tích hợp OAuth2 và JWT**:
  - OAuth2: Hỗ trợ đăng nhập với Google và Facebook thông qua OAuth2, cho phép người dùng dễ dàng truy cập hệ thống với tài khoản hiện có của họ.
  - JWT: Sau khi đăng nhập, JWT sẽ được sử dụng để quản lý phiên làm việc, đảm bảo mỗi yêu cầu của người dùng được xác thực và bảo mật.



## 🌟 Lợi Ích Khi Sử Dụng Koi Express

- **An Toàn và Đảm Bảo**: Chúng tôi hiểu rằng việc vận chuyển cá Koi là rất nhạy cảm. Koi Express cam kết vận chuyển cá Koi của bạn trong môi trường an toàn, không gây căng thẳng và với sự chăm sóc tốt nhất.

- **Chi Phí Hợp Lý**: Hệ thống tự động tính toán chi phí dựa trên thông tin chính xác, giúp bạn tối ưu hóa ngân sách cho việc vận chuyển.

- **Dịch Vụ Chăm Sóc Tận Tâm**: Khách hàng có thể liên hệ với đội ngũ chăm sóc khách hàng bất cứ lúc nào để được hỗ trợ.
