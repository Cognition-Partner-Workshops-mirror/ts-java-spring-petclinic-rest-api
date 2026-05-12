# Dockerfile for the Vet microservice.
# Build the JAR first: mvn clean package -DskipTests
# Then build the image: docker build -t vet-service .
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/vet-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
