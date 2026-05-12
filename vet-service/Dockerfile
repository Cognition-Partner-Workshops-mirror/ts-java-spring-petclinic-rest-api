# Multi-stage Dockerfile for the Vet microservice
# Stage 1: Build the application using Maven
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Install Maven (Alpine-based image does not include it)
RUN apk add --no-cache maven \
    && mvn clean package -DskipTests -q

# Stage 2: Run the application with a minimal JRE image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the default server port
EXPOSE 8080

# Run with the dev profile by default; override via SPRING_PROFILES_ACTIVE env var
ENTRYPOINT ["java", "-jar", "app.jar"]
