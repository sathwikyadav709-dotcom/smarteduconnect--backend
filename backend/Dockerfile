# Multi-stage Dockerfile for Smart Campus Management Backend
# Stage 1: Build application using Maven and JDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Cache maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal JRE runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Add a non-privileged user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy jar from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Dynamic port binding for Render/cloud environments
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
