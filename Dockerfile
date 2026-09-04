# --- Stage 1: Build the application ---
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

# Copy the source code and build files
COPY pom.xml .
COPY src ./src

# Compile and package the application, skipping tests for faster builds
RUN mvn clean package -DskipTests

# --- Stage 2: Create the runtime image ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the packaged JAR file from the builder stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 53400
ENTRYPOINT ["java", "-jar", "app.jar"]