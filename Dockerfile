# STAGE 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies by copying only pom.xml first
COPY pom.xml .
RUN mvn dependency:go-offline

# Compile and package the application
COPY src ./src
RUN mvn clean package -DskipTests

# STAGE 2: Lightweight Runtime Environment
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy artifact from the build stage for minimal image size
COPY --from=build /app/target/*.jar app.jar

# Application port (must match server.port in properties)
EXPOSE 7777

ENTRYPOINT ["java", "-jar", "app.jar"]