FROM eclipse-temurin:21 AS builder

WORKDIR /build

COPY gradle/ gradle/
COPY gradlew gradlew.bat ./
COPY build.gradle settings.gradle ./
COPY database/ database/
COPY service/ service/

RUN ./gradlew :service:bootJar -x test --no-daemon

FROM eclipse-temurin:21

COPY --from=builder /build/service/build/libs/service.jar /app/service.jar

CMD ["java", "-jar", "/app/service.jar"]
