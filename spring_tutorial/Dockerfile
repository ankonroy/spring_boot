# Use OpenJDK 21 as base image (matches your project)
FROM eclipse-temurin:21-jdk-alpine

# Install Maven for building
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy the entire project source
COPY . .

# Build the JAR file
RUN mvn clean package -Dmaven.test.skip=true

# Copy the built JAR (now it exists in the container)
RUN cp target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]