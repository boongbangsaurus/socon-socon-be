# 빌드 스테이지
FROM amazoncorretto:17.0.7-alpine AS builder
USER root
WORKDIR /utils
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY gradle.properties .
COPY settings.gradle .
COPY src src
# gradlew 실행 권한 부여
RUN chmod +x ./gradlew
RUN ./gradlew clean build
RUN ./gradlew publish