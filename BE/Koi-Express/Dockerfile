# Step 1: Build the application with Maven and Amazon Corretto
FROM maven:3.9.9-amazoncorretto-21 AS build

# Step 2: Set the working directory inside the container for the build
WORKDIR /app

# Step 3: Copy the Maven project files (pom.xml and src directory)
COPY pom.xml .
COPY src ./src

# Step 4: Package the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Step 5: Use a lightweight Amazon Corretto JDK image to run the app
FROM amazoncorretto:21.0.4-alpine

# Step 6: Set the working directory for the running app
WORKDIR /app

# Step 7: Copy only the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Step 8: Expose the port the application will run on
EXPOSE 8080

# Step 9: Add a health check
HEALTHCHECK --interval=30s --timeout=30s --start-period=10s --retries=3 \
  CMD curl --fail http://localhost:8080/actuator/health || exit 1

# Step 10: Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
