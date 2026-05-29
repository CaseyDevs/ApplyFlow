FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# Pre-fetch dependencies for faster startup
RUN ./gradlew --no-daemon dependencies || true

COPY src src

EXPOSE 8080

CMD ["./gradlew", "--no-daemon", "bootRun"]
