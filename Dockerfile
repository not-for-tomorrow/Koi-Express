# Bước 1: Xây dựng ứng dụng frontend (React/Vite)
FROM node:18 AS build-frontend
WORKDIR /app/FE
COPY FE/Koi-Express/package.json FE/Koi-Express/package-lock.json .  # Sao chép package.json và package-lock.json của frontend
RUN npm install                                                     # Cài đặt dependencies cho frontend
COPY FE/Koi-Express .                                               # Sao chép tất cả file frontend
RUN npm run build                                                   # Xây dựng ứng dụng frontend

# Bước 2: Xây dựng ứng dụng backend (Spring Boot)
FROM maven:3.9.9-amazoncorretto-21 AS build-backend
WORKDIR /app
COPY BE/Koi-Express/pom.xml .                                       # Sao chép file pom.xml của backend
COPY BE/Koi-Express/src ./src                                       # Sao chép mã nguồn backend
RUN mvn clean package -DskipTests                                   # Xây dựng ứng dụng backend

# Bước 3: Tạo container cuối cùng để chạy ứng dụng
FROM amazoncorretto:21.0.4-alpine
WORKDIR /app

# Sao chép file JAR đã build từ backend
COPY --from=build-backend /app/target/*.jar app.jar

# Sao chép frontend đã build vào thư mục public để phục vụ tệp tĩnh
COPY --from=build-frontend /app/FE/dist /app/public

# Expose cổng 8080 cho backend
EXPOSE 8080

# Health check cho backend
HEALTHCHECK --interval=30s --timeout=30s --start-period=10s --retries=3 \
  CMD curl --fail http://localhost:8080/actuator/health || exit 1

# Chạy ứng dụng backend
ENTRYPOINT ["java", "-jar", "app.jar"]
