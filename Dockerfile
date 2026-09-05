# syntax=docker/dockerfile:1

# Stage 1: Build the application using maven
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /build

COPY mvnw mvnw
COPY .mvn/ .mvn/
COPY pom.xml pom.xml

RUN ./mvnw dependency:go-offline -DskipTests

COPY src/ src/
RUN ./mvnw package -DskipTests

# Stage 2: Run the application using a minimal JRE image
FROM eclipse-temurin:17-jre-jammy AS final

WORKDIR /app

# Create a non-privileged user for security best practices
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

# Copy the built jar from the build stage
COPY --from=build /build/target/Ramashish.booking-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]