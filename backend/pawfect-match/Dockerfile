# Stage 1: Build the application
FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# Download dependencies to cache them
RUN mvn dependency:go-offline -B
COPY src ./src
# Build the application, skipping tests to speed up the process
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jdk-slim
WORKDIR /app
# Copy the JAR from the builder stage
COPY --from=builder /app/target/pawfect-match-0.0.1-SNAPSHOT.jar app.jar
# Expose the application port
EXPOSE 8080
# Set environment variable for Spring Boot port (configurable via PORT)
ENV PORT=8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]