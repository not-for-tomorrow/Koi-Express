# Step 1: Use a base image with Java
FROM openjdk:17-jdk-alpine

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the project JAR file into the container
# Assuming the JAR file is built as "koi-express.jar" after using Maven or Gradle
COPY target/Koi-Express-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Expose the port the application will run on
EXPOSE 8080

# Step 5: Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
