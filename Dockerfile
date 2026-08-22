# =========================================================
# Stage 1: Build Spring Boot Application
# =========================================================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

# Working directory
WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application
# Tests are skipped to make deployment faster
RUN mvn clean package -DskipTests


# =========================================================
# Stage 2: Run Spring Boot Application
# =========================================================
FROM eclipse-temurin:17-jre

# Working directory
WORKDIR /app

# Copy the generated JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Render/Spring Boot will use the PORT environment variable
EXPOSE 8080

# Start Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
