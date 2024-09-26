
# 🐟 **Koi Express - Hệ Thống Quản Lý Vận Chuyển Cá Koi**

Koi Express là hệ thống vận chuyển cá Koi chuyên nghiệp, cung cấp dịch vụ vận chuyển cá Koi trong nước với các tiêu chuẩn an toàn cao nhất. Hệ thống hỗ trợ khách hàng trong toàn bộ quy trình, từ khi đặt đơn, tính toán chi phí, kiểm tra sức khỏe cá, đến theo dõi quá trình vận chuyển và nhận phản hồi sau khi giao hàng.

![Koi Express](./assets/images/Logo.jpg)

## 🚀 **Tính Năng Chính**
- **Quản Lý Đơn Hàng Vận Chuyển**: Khách hàng có thể dễ dàng đặt đơn vận chuyển cá Koi, cung cấp thông tin đầy đủ về loại cá, trọng lượng và kích thước.
  
- **Tính Toán Chi Phí Vận Chuyển**: Hệ thống tự động tính toán chi phí dựa trên các yếu tố như trọng lượng, kích thước, khoảng cách vận chuyển, và các dịch vụ bổ sung như bảo hiểm, chăm sóc đặc biệt, kiểm tra sức khỏe.

- **Theo Dõi Trạng Thái Vận Chuyển**: Cung cấp khả năng theo dõi trạng thái đơn hàng theo thời gian thực từ quá trình đóng gói đến khi giao hàng, giúp khách hàng và nhân viên nắm bắt chính xác tình hình.

- **Quản Lý Nhân Sự**: Hệ thống phân quyền với nhiều vai trò khác nhau như Khách hàng, Nhân viên chăm sóc khách hàng, Nhân viên giao hàng và Quản lý, đảm bảo quản lý quy trình vận chuyển hiệu quả.

- **Phản Hồi và Báo Cáo**: Khách hàng có thể gửi phản hồi về chất lượng dịch vụ, và các quản lý có thể dễ dàng xem báo cáo về hiệu suất vận chuyển, hiệu quả nhân viên và phản hồi từ khách hàng.

## 🛠️ **Công Nghệ Sử Dụng**
### **Frontend**
- **ReactJS**: Framework JavaScript mạnh mẽ giúp xây dựng giao diện người dùng hiện đại, tương tác.
- **Vite**: Công cụ build cực nhanh, giúp cải thiện hiệu suất và trải nghiệm phát triển ứng dụng web.
- **Tailwind CSS**: Framework CSS utility-first giúp tạo giao diện đẹp và dễ dàng tùy chỉnh, đảm bảo tính nhất quán trong thiết kế và responsive trên mọi thiết bị.
- **Axios**: Thư viện giúp xử lý các yêu cầu HTTP dễ dàng và linh hoạt, được sử dụng để gọi các API cho việc lấy dữ liệu đơn hàng, giá cả, và trạng thái vận chuyển.
- **React Router**: Quản lý các routes trong ứng dụng, cung cấp điều hướng linh hoạt giữa các trang như trang đặt hàng, trạng thái vận chuyển, và hồ sơ khách hàng.

### **Backend**
- **Spring Boot**: Framework mạnh mẽ giúp xây dựng API RESTful và các dịch vụ backend. Hỗ trợ dễ dàng trong việc phát triển ứng dụng có khả năng mở rộng.
- **JWT (JSON Web Token)**: Được sử dụng để xác thực người dùng, đảm bảo tính bảo mật trong các yêu cầu API bằng cách mã hóa và kiểm tra quyền truy cập với các token JSON an toàn.
- **MySQL**: Cơ sở dữ liệu quan hệ mạnh mẽ dùng để lưu trữ thông tin khách hàng, đơn hàng, và dữ liệu vận chuyển.

### **Tích Hợp OAuth2 và JWT**
- **OAuth2**: Hỗ trợ đăng nhập với Google và Facebook thông qua OAuth2, cho phép người dùng dễ dàng truy cập hệ thống với tài khoản hiện có của họ.
- **JWT**: Sau khi đăng nhập, JWT sẽ được sử dụng để quản lý phiên làm việc, đảm bảo mỗi yêu cầu của người dùng được xác thực và bảo mật.

### **Docker**
- **Docker**: Hệ thống được đóng gói và triển khai thông qua Docker containers, giúp quá trình cài đặt và triển khai trở nên nhanh chóng và nhất quán. Docker hỗ trợ việc cấu hình môi trường phát triển và sản xuất với tính ổn định cao.
   - Để chạy hệ thống với Docker, bạn chỉ cần:
     ```bash
     docker-compose up
     ```
   - Cấu hình Docker gồm 2 service chính:
     - **Backend Service**: Chạy ứng dụng Spring Boot.
     - **Database Service**: Chạy MySQL database cho ứng dụng.
   - File `docker-compose.yml` sẽ tự động tạo môi trường cần thiết bao gồm cả backend và MySQL.

### **DBeaver**
- **DBeaver**: Công cụ quản lý cơ sở dữ liệu trực quan, được sử dụng để kết nối với MySQL và thực hiện quản lý dữ liệu. DBeaver giúp dễ dàng theo dõi và kiểm tra cấu trúc dữ liệu, chạy truy vấn SQL, và theo dõi hiệu suất cơ sở dữ liệu.
   - Kết nối DBeaver với MySQL thông qua thông tin sau:
     - Host: `localhost`
     - Port: `3306`
     - Database: `koi_express`
     - User: `root`
     - Password: `yourpassword`

## 🌟 **Lợi Ích Khi Sử Dụng Koi Express**
- **An Toàn và Đảm Bảo**: Chúng tôi hiểu rằng việc vận chuyển cá Koi là rất nhạy cảm. Koi Express cam kết vận chuyển cá Koi của bạn trong môi trường an toàn, không gây căng thẳng và với sự chăm sóc tốt nhất.
  
- **Chi Phí Hợp Lý**: Hệ thống tự động tính toán chi phí dựa trên thông tin chính xác, giúp bạn tối ưu hóa ngân sách cho việc vận chuyển.

- **Dịch Vụ Chăm Sóc Tận Tâm**: Đội ngũ chăm sóc khách hàng của chúng tôi luôn sẵn sàng hỗ trợ bạn trong suốt quá trình vận chuyển, từ khi đặt đơn đến khi cá đến nơi an toàn.

## 📈 **Cách Cài Đặt**
### Yêu Cầu Hệ Thống:
- **Node.js** (>= 14.x)
- **Java** (>= 11.x)
- **MySQL**
- **Maven**
- **Docker** (nếu sử dụng Docker để chạy hệ thống)
- **DBeaver** (nếu cần công cụ quản lý cơ sở dữ liệu)

### Hướng Dẫn Cài Đặt:
1. **Clone Repository**:
   ```bash
   git clone https://github.com/your-repo/koi-express.git
   cd koi-express
   ```

2. **Frontend**: Cài đặt các gói npm và chạy ứng dụng React.
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

3. **Backend**: Build và chạy ứng dụng Spring Boot.
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

4. **Cấu Hình MySQL**: Cập nhật thông tin cơ sở dữ liệu trong `application.properties`.
   ```properties
   spring:
    datasource:
      url: jdbc:mysql://localhost:3306/koi_express
      username: root
      password: yourpassword
      driver-class-name: com.mysql.cj.jdbc.Driver
   ```

5. **Tích Hợp OAuth2 và JWT**: Cấu hình thông tin OAuth2 và JWT trong file cấu hình.
   ```properties
   pring:
    security:
      oauth2:
        client:
          registration:
            google:
              client-id: your-client-id
              client-secret: your-client-secret
              scope:
                - profile
                - email
            facebook:
              client-id: your-client-id
              client-secret: your-client-secret
              scope:
                - public_profile
                - email

   jwt:
    secret-key: "your-jwt-secret-key"
   ```

6. **Docker**: Sử dụng Docker để khởi chạy ứng dụng.
   ```bash
   docker-compose up
   ```

## 🛠️ **Cấu Trúc Thư Mục**
- **frontend/**: Chứa mã nguồn giao diện người dùng.
  - **src/components/**: Các component giao diện chính như form đặt hàng, bảng trạng thái vận chuyển.
  - **src/services/**: Các service dùng Axios để gọi API cho backend.
  - **src/routes/**: Định nghĩa các routes của ứng dụng, điều hướng giữa các trang bằng React Router.
  - **src/styles/**: Cấu hình và sử dụng các lớp CSS của Tailwind để tạo giao diện.
  
- **backend/**: Chứa mã nguồn dịch vụ backend.
  - **src/main/java/**: Chứa các controller, service, repository và các lớp quản lý logic của ứng dụng.
  - **src/main/resources/**: File cấu hình Spring Boot như `application.properties`.

## 🤝 **Đóng Góp**
Chúng tôi hoan nghênh sự đóng góp từ cộng đồng! Hãy tạo một pull request hoặc mở issue nếu bạn có ý tưởng hay cần giải quyết lỗi.
