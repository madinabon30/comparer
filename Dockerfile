# -------- BUILD STAGE --------
FROM gradle:8.7-jdk21 AS build

WORKDIR /app

# Copy Gradle wrapper & config first (better layer caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Download dependencies (no source yet)
RUN ./gradlew --no-daemon dependencies

# Copy application source
COPY src src

# Build Spring Boot fat jar
RUN ./gradlew --no-daemon bootJar


# -------- RUNTIME STAGE --------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
