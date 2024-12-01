# 빌드 스테이지
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app

# Gradle 래퍼와 소스 코드 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 프로젝트 빌드
RUN ./gradlew build

# 실행 스테이지
FROM eclipse-temurin:21-jre-alpine
COPY --from=build /workspace/app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]