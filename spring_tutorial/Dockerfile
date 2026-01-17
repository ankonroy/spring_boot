# Use OpenJDK 21 as base image (matches your project)
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY target/spring_tutorial-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application - Use absolute path to be safe
ENTRYPOINT ["java", "-jar", "/app/app.jar"]