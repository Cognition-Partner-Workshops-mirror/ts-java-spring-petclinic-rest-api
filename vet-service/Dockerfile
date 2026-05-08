# Multi-stage build for the Vet Service microservice
# Stage 1: Build the application with Maven
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests -q

# Stage 2: Run the application with a lightweight JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy the built JAR from the build stage
COPY --from=build /app/target/vet-service-*.jar app.jar

# Expose the service port
EXPOSE 8083

# Health check using actuator endpoint
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget -qO- http://localhost:8083/api/actuator/health || exit 1

# Run the application with the prod profile when using Docker Compose
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
