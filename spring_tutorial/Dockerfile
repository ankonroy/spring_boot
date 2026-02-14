# Multi-stage build for Spring Boot application
# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml ./

# Download dependencies
RUN mvn dependency:resolve -DskipTests

# Copy source code and build
COPY src ./src/

# Build the JAR file (skip tests for faster build)
RUN mvn clean package -DskipTests -f pom.xml

# Stage 2: Runtime image - smaller and more secure
FROM eclipse-temurin:21-jre-alpine

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Expose application port
EXPOSE 8080

# Health check (adjust URL if needed based on your actuator config)
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Optional: JVM tuning arguments
# ENV JAVA_OPTS="-Xms256m -Xmx512m"
# ENTRYPOINT ["java", "-jar", "/app/app.jar", "${JAVA_OPTS}"]

