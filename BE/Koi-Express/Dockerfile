# Step 1: Build the application with Maven 3.9.9 and Amazon Corretto 21
FROM maven:3.9.9-amazoncorretto-21 AS build

# Step 2: Set the working directory inside the container for the build
WORKDIR /app

# Step 3: Copy the Maven project files (pom.xml and src directory)
COPY pom.xml .
COPY src ./src

# Step 4: Package the application (skip tests for faster build, if necessary)
RUN mvn clean package -DskipTests

# Step 5: Use a lightweight Amazon Corretto JDK 21 image for running the app
FROM amazoncorretto:21.0.4

# Step 6: Set the working directory for the running app
WORKDIR /app

# Step 7: Copy the JAR file from the build stage
COPY --from=build /app/target/Koi-Express-0.0.1-SNAPSHOT.jar app.jar

# Step 8: Expose the port the application will run on
EXPOSE 8080

# Step 9: Add a health check (optional but recommended)
HEALTHCHECK --interval=30s --timeout=30s --start-period=10s --retries=3 \
  CMD curl --fail http://localhost:8080/actuator/health || exit 1

# Step 10: Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
